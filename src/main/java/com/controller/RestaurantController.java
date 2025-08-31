package com.controller;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;

import com.model.*;
import com.view.UIMenu;
import com.service.*;
import com.utils.Helpers;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RestaurantController {

    private final UIMenu uiMenu;
    private final InputService input;
    private final Screen screen;
    private final Terminal terminal;

    private static final String CSV_PATH = "data/restaurants.csv";
    private static final Pattern REST_ID_RX = Pattern.compile("^MIC-(\\d{6})$");

    public RestaurantController(Terminal terminal, Screen screen) {

        this.uiMenu = new UIMenu(terminal, screen);
        this.input = new InputService(terminal, screen);
        this.screen = screen;
        this.terminal = terminal;
    }

    //Aggiunge un nuovo ristorante e lo salva nel CSV
    public Restaurant addRestaurant(Role role, List<String> opzioni, int selezione) {

        try {

            TerminalManager.clearScreen();
            uiMenu.drawMenu(opzioni, selezione, "AGGIUNGI RISTORANTE");

            // Consenti solo agli ADMIN

            if (role != Role.ADMIN) {

                uiMenu.showMessage("Solo un admin può aggiungere ristoranti.");
                waitEnter();
                return null;
            }

            // Ricava l'ID-Admin dell'admin corrente
            String adminId = resolveCurrentAdminId();

            if (adminId == null) {

                uiMenu.showMessage("Impossibile identificare l'admin corrente. Operazione annullata.");
                waitEnter();
                return null;
            }

            String name = input.readLine("Nome: ");
            String address = input.readLine("Indirizzo: ");
            String location = input.readLine("Località: ");
            String price = input.readLine("Prezzo medio (es. €€): ");
            String cuisine = input.readLine("Cucina: ");
            String longitude = input.readLine("Longitudine: ");
            String latitude = input.readLine("Latitudine: ");
            boolean delivery = readYesNo("Delivery disponibile (s/n)? ");
            boolean online = readYesNo("Prenotazione online (s/n)? ");

            // Calcola nuovo ID ristorante
            String newId = nextRestaurantId();

            // Appendi al CSV con ID-Admin
            appendCsvRow(newId, adminId, name, address, location, price, cuisine,
                    longitude, latitude, delivery, online);

            // Costruisci oggetto Restaurant 
            Restaurant r = new Restaurant(name, address);
            r.setDelivery(delivery);
            r.setOnlineReservation(online);

            uiMenu.showMessage("\nRistorante aggiunto con ID: " + newId + " (Admin: " + adminId + ")");
            uiMenu.showMessage("Premi Invio per continuare...");
            waitEnter();

            return r;
        } catch (Exception e) {
            throw new RuntimeException("Errore durante addRestaurant", e);
        }
    }

    //Elimina il ristorante scelto utilizzando ID
    public Restaurant delRestaurant(Role role, List<String> opzioni, int selezione) {

        try {

            TerminalManager.clearScreen();
            uiMenu.drawMenu(opzioni, selezione, "ELIMINA RISTORANTE");

            String id = input.readLine("Inserisci ID ristorante (es. MIC-000123): ").trim();
            Path path = Paths.get(CSV_PATH);
            CsvService.CsvTable table = CsvService.read(path, true);

            if (table.getRows().isEmpty()) {

                uiMenu.showMessage("Nessun ristorante presente.");
                waitEnter();
                return null;
            }

            int idxId = col(table, "ID", 0);
            List<List<String>> kept = new ArrayList<>();
            boolean removed = false;

            for (List<String> row : table.getRows()) {
                if (row.isEmpty()) continue;
                String rowId = (idxId < row.size()) ? Helpers.safe(row.get(idxId)) : "";
                if (rowId.equals(id)) {
                    removed = true;
                } else {
                    kept.add(row);
                }
            }

            if (removed) {

                CsvService.rewrite(path, table.getHeader(), kept);
                uiMenu.showMessage("Ristorante con ID " + id + " eliminato.");
            } else {

                uiMenu.showMessage("Nessun ristorante trovato con ID " + id);
            }

            waitEnter();
            return null;

        } catch (Exception e) {
            throw new RuntimeException("Errore durante delRestaurant", e);
        }
    }

   //Mostra solo i ristoranti dell'admin corrente (A per andare avanti, S per tornare indietro)
    public Restaurant viewRestaurantDetails(Role role, List<String> opzioni, int selezione) {

        try {

            TerminalManager.clearScreen();
            uiMenu.drawMenu(opzioni, selezione, "I TUOI RISTORANTI");

            if (role != Role.ADMIN) {

                uiMenu.showMessage("Solo un admin può visualizzare i propri ristoranti.");
                waitEnter();
                return null;
            }

            // 1) Ricavo l'utente admin corrente (riuso anche per risposte alle recensioni)

            Users adminUser = resolveCurrentAdminUser();

            if (!(adminUser instanceof Adm admin)) {

                waitEnter();
                return null;
            }

            String adminId = admin.getStringId();

            // 2) Carico i ristoranti filtrando per ID-Admin (via CsvService)
            List<RestaurantRow> myRestaurants = loadRestaurantsByAdmin(adminId);
            if (myRestaurants.isEmpty()) {
                uiMenu.showMessage("Non hai ristoranti salvati.");
                waitEnter();
                return null;
            }

            // 3) Paginazione
            final int PAGE_SIZE = 20;
            int page = 0;

            while (true) {
                // Disegna pagina
                renderRestaurantsPage(myRestaurants, page, PAGE_SIZE);

                // Input: numero, 'A' avanti, 'D' indietro
                String cmd = input.readLine("Seleziona [1-" + Math.min(PAGE_SIZE, myRestaurants.size() - page * PAGE_SIZE) + "] / A avanti / D indietro / Q per uscire: ");
                if (cmd == null) continue;
                cmd = cmd.trim();

                if (cmd.equalsIgnoreCase("Q")) {
                    TerminalManager.clearScreen();
                    return null;
                }
                if (cmd.equalsIgnoreCase("A")) {
                    int maxPage = (myRestaurants.size() - 1) / PAGE_SIZE;
                    if (page < maxPage) page++;
                    continue;
                }
                if (cmd.equalsIgnoreCase("S")) {
                    if (page > 0) page--;
                    continue;
                }

                // Numero?
                try {
                    int choice = Integer.parseInt(cmd);
                    if (choice < 1 || choice > PAGE_SIZE) {
                        uiMenu.showMessage("Numero fuori range della pagina.");
                        continue;
                    }
                    int idx = page * PAGE_SIZE + (choice - 1);
                    if (idx < 0 || idx >= myRestaurants.size()) {
                        uiMenu.showMessage("Selezione non valida.");
                        continue;
                    }

                    // 4) Mostra dettagli del ristorante scelto
                    RestaurantRow row = myRestaurants.get(idx);
                    showRestaurantDetails(row);

                    uiMenu.showMessage("Premi R per recensioni, oppure Invio per tornare alla lista.");
                    while (true) {
                        KeyStroke k = screen.readInput();
                        if (k == null) continue;
                        if (k.getKeyType() == KeyType.Enter) break;
                        Character ch = k.getCharacter();
                        if (ch != null && (ch == 'r' || ch == 'R')) {
                            // Apri il menu recensioni lato admin
                            FeedbackController fc = new FeedbackController(terminal, screen);
                            fc.openMenuForRestaurant(row.id, adminUser, Role.ADMIN);
                            // dopo il menu, ridisegno i dettagli del ristorante
                            TerminalManager.clearScreen();
                            showRestaurantDetails(row);
                            uiMenu.showMessage("Premi R per recensioni, oppure Invio per tornare alla lista.");
                        }
                    }

                    // torna alla lista
                    TerminalManager.clearScreen();
                    uiMenu.drawMenu(opzioni, selezione, "I TUOI RISTORANTI");

                } catch (NumberFormatException nfe) {
                    uiMenu.showMessage("Comando non valido.");
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Errore durante viewRestaurantDetails", e);
        }
    }


    //Helper CSV

    private static class RestaurantRow {

        // Ordine CSV: ID,ID-Admin,Name,Address,Location,Price,Cuisine,Longitude,Latitude,delivery,online
        String id;
        String adminId;
        String name;
        String address;
        String location;
        String price;
        String cuisine;
        String longitude;
        String latitude;
        String delivery; // "sì"/"no"
        String online;   // "sì"/"no"
    }

    private List<RestaurantRow> loadRestaurantsByAdmin(String adminId) throws IOException {

        Path path = Paths.get(CSV_PATH);
        CsvService.CsvTable table = CsvService.read(path, true);

        List<RestaurantRow> out = new ArrayList<>();
        if (table.getRows().isEmpty()) return out;

        // Indici robusti (se c'è header lo usiamo, altrimenti fallback fissi)
        int idxId       = col(table, "ID", 0);
        int idxAdmin    = col(table, "ID-Admin", 1);
        int idxName     = col(table, "Name", 2);
        int idxAddress  = col(table, "Address", 3);
        int idxLocation = col(table, "Location", 4);
        int idxPrice    = col(table, "Price", 5);
        int idxCuisine  = col(table, "Cuisine", 6);
        int idxLon      = col(table, "Longitude", 7);
        int idxLat      = col(table, "Latitude", 8);
        int idxDel      = col(table, "delivery", 9);
        int idxOnl      = col(table, "online", 10);

        for (List<String> c : table.getRows()) {

            if (c.size() < 11) continue;
            String rowAdmin = Helpers.safe(c.get(idxAdmin));
            if (!rowAdmin.equals(adminId)) continue;

            RestaurantRow r = new RestaurantRow();
            r.id        = Helpers.safe(c.get(idxId));
            r.adminId   = rowAdmin;
            r.name      = Helpers.safe(c.get(idxName));
            r.address   = Helpers.safe(c.get(idxAddress));
            r.location  = Helpers.safe(c.get(idxLocation));
            r.price     = Helpers.safe(c.get(idxPrice));
            r.cuisine   = Helpers.safe(c.get(idxCuisine));
            r.longitude = Helpers.safe(c.get(idxLon));
            r.latitude  = Helpers.safe(c.get(idxLat));
            r.delivery  = Helpers.safe(c.get(idxDel));
            r.online    = Helpers.safe(c.get(idxOnl));
            out.add(r);

        }
        return out;
    }

    /** Disegna a schermo un elenco di max 20 ristoranti (nome + ID) */
    private void renderRestaurantsPage(List<RestaurantRow> rows, int page, int pageSize) {

        try {

            // area semplice sotto al banner, senza dipendere da UIMenu internals
            TextGraphics tg = screen.newTextGraphics();
            TerminalSize size = screen.getTerminalSize();
            int cols = size.getColumns();
            int left = 4;
            int top = Math.max(8, (int)Math.floor(size.getRows()*0.25)); // area approx sotto al titolo

            // pulizia area stimata
            int height = Math.min(26, Math.max(10, size.getRows() - top - 4));
            tg.setForegroundColor(TextColor.ANSI.DEFAULT);
            tg.fillRectangle(new com.googlecode.lanterna.TerminalPosition(left, top),
                    new TerminalSize(Math.max(10, cols - left - 4), height), ' ');

            int start = page * pageSize;
            int end = Math.min(rows.size(), start + pageSize);

            tg.setForegroundColor(TextColor.ANSI.CYAN);
            tg.enableModifiers(SGR.BOLD);
            tg.putString(left, top, String.format("Pagina %d/%d — Totale ristoranti: %d",
                    page + 1, Math.max(1, (rows.size() + pageSize - 1) / pageSize), rows.size()));
            tg.disableModifiers(SGR.BOLD);

            int row = top + 2;
            for (int i = start; i < end; i++) {
                int num = i - start + 1; // 1..pageSize
                RestaurantRow r = rows.get(i);
                String line = String.format("%2d) %-40s  [%s]", num, Helpers.truncate(r.name, 40), r.id);
                tg.putString(left, row++, line);
            }

            row++;
            tg.putString(left, row++, "Comandi: numero per dettagli | A = avanti | D = indietro | Q = esci");
            screen.refresh();

        } catch (Exception ignored) {}
    }

    /** Mostra una scheda dettagli del ristorante selezionato */
    private void showRestaurantDetails(RestaurantRow r) {

        try {
            
            TextGraphics tg = screen.newTextGraphics();
            TerminalSize size = screen.getTerminalSize();
            int cols = size.getColumns();
            int left = 4;
            int top = Math.max(8, (int)Math.floor(size.getRows()*0.25));

            // pulizia area
            int height = Math.min(26, Math.max(10, size.getRows() - top - 4));
            tg.setForegroundColor(TextColor.ANSI.DEFAULT);
            tg.fillRectangle(new com.googlecode.lanterna.TerminalPosition(left, top),
                    new TerminalSize(Math.max(10, cols - left - 4), height), ' ');

            tg.setForegroundColor(TextColor.ANSI.CYAN);
            tg.enableModifiers(SGR.BOLD);
            tg.putString(left, top, "DETTAGLI RISTORANTE");
            tg.disableModifiers(SGR.BOLD);

            int row = top + 2;
            tg.putString(left, row++, "ID: " + r.id);
            tg.putString(left, row++, "ID-Admin: " + r.adminId);
            tg.putString(left, row++, "Nome: " + r.name);
            tg.putString(left, row++, "Indirizzo: " + r.address);
            tg.putString(left, row++, "Località: " + r.location);
            tg.putString(left, row++, "Prezzo: " + r.price);
            tg.putString(left, row++, "Cucina: " + r.cuisine);
            tg.putString(left, row++, "Longitudine: " + r.longitude);
            tg.putString(left, row++, "Latitudine: " + r.latitude);
            tg.putString(left, row++, "Delivery: " + r.delivery);
            tg.putString(left, row++, "Prenotazione online: " + r.online);

            screen.refresh();
        } catch (Exception ignored) {}
    }

    // ========================
    // Supporto CSV / ID
    // ========================

    private void appendCsvRow(String id, String adminId, String name, String address, String location,
                              String price, String cuisine,
                              String longitude, String latitude,
                              boolean delivery, boolean online) throws IOException {

        List<String> header = List.of("ID","ID-Admin","Name","Address","Location","Price","Cuisine","Longitude","Latitude","delivery","online");
        List<String> row = List.of(
                id,
                adminId,
                name,
                address,
                location,
                price,
                cuisine,
                longitude,
                latitude,
                delivery ? "sì" : "no",
                online ? "sì" : "no"
        );

        CsvService.appendRow(Paths.get(CSV_PATH), row, header);
    }

    /** ID ristorante incrementale robusto: max MIC-xxxxxx dalla colonna ID via CsvService */
    private String nextRestaurantId() throws IOException {
        Path path = Paths.get(CSV_PATH);
        CsvService.CsvTable table = CsvService.read(path, true);

        int max = 0;
        int idxId = col(table, "ID", 0);

        for (List<String> row : table.getRows()) {
            if (row.isEmpty() || idxId >= row.size()) continue;
            String id = Helpers.safe(row.get(idxId));
            Matcher m = REST_ID_RX.matcher(id);
            if (m.matches()) {
                int num = Integer.parseInt(m.group(1));
                if (num > max) max = num;
            }
        }
        return String.format("MIC-%06d", max + 1);
    }

    /** Rileva s/n */
    private boolean readYesNo(String prompt) throws IOException {
        while (true) {
            String s = input.readLine(prompt).trim().toLowerCase();
            if (s.equals("s") || s.equals("si") || s.equals("sì") || s.equals("y") || s.equals("yes")) return true;
            if (s.equals("n") || s.equals("no")) return false;
            uiMenu.showMessage("Risposta non valida. Scrivi s/n.");
        }
    }

    private void waitEnter() throws IOException {
        while (true) {
            KeyStroke key = screen.readInput();
            if (key != null && key.getKeyType() == KeyType.Enter) break;
        }
    }


    /**
     * Ricava l'ID-Admin dell'admin corrente.
     * Strategia semplice: chiede conferma email (usata nel login) e legge da AuthService.
     * Se preferisci evitare il prompt, passa direttamente l'oggetto Adm a questo controller.
     */
    private String resolveCurrentAdminId() throws IOException {
        String email = input.readLine("Conferma la tua email admin: ");
        Users u = AuthService.loadUserByEmail(Role.ADMIN, email);
        if (u instanceof Adm a) {
            return a.getStringId(); // usa il tuo getter attuale
        }
        uiMenu.showMessage("Admin non trovato con l'email indicata.");
        return null;
    }

    /** Ritorna l'utente Admin corrente (chiede email) oppure null. */
    private Users resolveCurrentAdminUser() throws IOException {
        String email = input.readLine("Conferma la tua email admin: ");
        Users u = AuthService.loadUserByEmail(Role.ADMIN, email);
        if (u instanceof Adm) return u;
        uiMenu.showMessage("Admin non trovato con l'email indicata.");
        return null;
    }


    /** Restituisce l'indice di colonna dall'header se presente, altrimenti usa fallback. */
    private static int col(CsvService.CsvTable table, String name, int fallback) {
        int idx = table.columnIndex(name);
        return idx >= 0 ? idx : fallback;
    }
}
