package com.controller;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;

import com.model.*;
import com.view.UIMenu;
import com.service.*;
import com.utils.Helpers;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * SearchController: ricerca ristoranti combinando criteri.
 * CSV ristoranti: ID,ID-Admin,Name,Address,Location,Price,Cuisine,Longitude,Latitude,delivery,online
 * Feedback: data/feedback.csv -> ID-Feedback,mail,ID-Restaurant,Stars,Comment
 */

public class SearchController {

    private final UIMenu uiMenu;
    private final InputService input;
    private final Screen screen;
    private final Terminal terminal; 

    private static final String REST_CSV = "data/restaurants.csv";
    private static final String FEED_CSV = "data/feedback.csv";

    public SearchController(Screen screen, Terminal terminal) {
        this.uiMenu = new UIMenu(terminal, screen);
        this.input = new InputService(terminal, screen);
        this.screen = screen;
        this.terminal = terminal; 
    }

    //Flow di ricerca principale

    public void searchFlow(List<String> opzioni, int selezione) {

        try {
            TerminalManager.clearScreen();
            uiMenu.drawMenu(opzioni, selezione, "RICERCA RISTORANTI");

            // 1) Location OBBLIGATORIA
            String location = input.readLine("Località (obbligatoria): ").trim();
            while (location.isBlank()) {
                location = input.readLine("La località è obbligatoria. Inserisci località: ").trim();
            }

            // 2) Filtri OPZIONALI
            String cuisine = input.readLine("Cucina (invio per qualsiasi): ").trim();

            String priceRaw = input.readLine("Fascia prezzo (€/€€/€€€/€€€€ oppure 20/40/60/80, invio per qualsiasi): ").trim();
            Integer priceVal = parsePrice(priceRaw); // 20,40,60,80 oppure null

            Boolean delivery = readTriState("Delivery disponibile? (s/n/invio=qualsiasi): ");
            Boolean online   = readTriState("Prenotazione online? (s/n/invio=qualsiasi): ");

            Integer minStars = Helpers.parseIntOrNull(input.readLine("Stelle minime [1..5] (invio per ignorare): ").trim());
            if (minStars != null && (minStars < 1 || minStars > 5)) minStars = null;

            // 3) Carica e filtra
            Map<String, Double> avgStars = null;
            if (minStars != null && Files.exists(Paths.get(FEED_CSV))) {
                avgStars = loadAverageStars(); // MIC-id -> media
            }

            List<Row> all = loadRestaurants();
            List<Row> filtered = new ArrayList<>();
            for (Row r : all) {
                if (!containsIgnoreCase(r.location, location)) continue;
                if (!cuisine.isBlank() && !containsIgnoreCase(r.cuisine, cuisine)) continue;
                if (priceVal != null && !Objects.equals(priceToValue(r.price), priceVal)) continue;
                if (delivery != null && r.delivery != delivery) continue;
                if (online   != null && r.online   != online)   continue;

                if (minStars != null && avgStars != null) {
                    Double avg = avgStars.get(r.id);
                    if (avg == null || avg < minStars) continue;
                }
                filtered.add(r);
            }

            if (filtered.isEmpty()) {
                uiMenu.showMessage("Nessun ristorante trovato con i criteri indicati.");
                waitEnter();
                return;
            }

            // 4) Paginazione + dettagli
            paginateAndShow(filtered);

        } catch (Exception e) {
            throw new RuntimeException("Errore durante la ricerca", e);
        }
    }

    //Paginazione e mostra dei dettagli
    private void paginateAndShow(List<Row> rows) throws IOException {

        final int PAGE = 10;
        int page = 0;

        while (true) {
            renderPage(rows, page, PAGE);
            String cmd = input.readLine("Seleziona [1-" + Math.min(PAGE, rows.size() - page * PAGE) + "] / A avanti / D indietro / Q esci: ").trim();

            if (cmd.equalsIgnoreCase("Q")) {
                TerminalManager.clearScreen(); 
                return;
            }
            if (cmd.equalsIgnoreCase("A")) {
                int maxPage = (rows.size() - 1) / PAGE;
                if (page < maxPage) page++;
                continue;
            }
            if (cmd.equalsIgnoreCase("D")) {
                if (page > 0) page--;
                continue;
            }

            Integer choice = Helpers.parseIntOrNull(cmd);
            if (choice == null || choice < 1 || choice > PAGE) {
                uiMenu.showMessage("Comando non valido.");
                continue;
            }

            int idx = page * PAGE + (choice - 1);
            if (idx < 0 || idx >= rows.size()) {
                uiMenu.showMessage("Selezione fuori intervallo.");
                continue;
            }

            // Dettagli (con possibilità di aprire il menu recensioni)
            showDetails(rows.get(idx));

            uiMenu.showMessage("Premi Invio per tornare alla lista...");
            waitEnter();
            TerminalManager.clearScreen();
        }
    }

    private void renderPage(List<Row> rows, int page, int pageSize) {

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

            int start = page * pageSize;
            int end = Math.min(rows.size(), start + pageSize);

            tg.setForegroundColor(TextColor.ANSI.CYAN);
            tg.enableModifiers(SGR.BOLD);
            tg.putString(left, top, String.format("Pagina %d/%d — Risultati: %d",
                    page + 1, Math.max(1, (rows.size() + pageSize - 1) / pageSize), rows.size()));
            tg.disableModifiers(SGR.BOLD);

            int row = top + 2;
            for (int i = start; i < end; i++) {

                int num = i - start + 1;
                Row r = rows.get(i);
                String flags = String.format("Del:%s Onl:%s", yesNo(r.delivery), yesNo(r.online));
                String line = String.format("%2d) %-35s  %-10s  %-16s  [%s]",
                        num, Helpers.truncate(r.name, 35), r.price, Helpers.truncate(r.cuisine, 16), r.id);
                tg.putString(left, row++, line);
                tg.putString(left + 4, row++, "Loc: " + Helpers.truncate(r.location, 60) + "  |  " + flags);
            }
            screen.refresh();
        } catch (Exception ignored) {}
    }

    //Mostra i dettagli dei ristoranti (premere R per vedere le recensioni)
    private void showDetails(Row r) {

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
            tg.putString(left, row++, "Nome: " + r.name);
            tg.putString(left, row++, "Indirizzo: " + r.address);
            tg.putString(left, row++, "Località: " + r.location);
            tg.putString(left, row++, "Prezzo: " + r.price + "  (→ " + priceToValue(r.price) + ")");
            tg.putString(left, row++, "Cucina: " + r.cuisine);
            tg.putString(left, row++, "Longitudine: " + r.longitude);
            tg.putString(left, row++, "Latitudine: " + r.latitude);
            tg.putString(left, row++, "Delivery: " + yesNo(r.delivery));
            tg.putString(left, row++, "Prenotazione online: " + yesNo(r.online));
            screen.refresh();

            //Gestione tasto 'R' per aprire il menu recensioni
            uiMenu.showMessage("Premi R per recensioni, oppure Invio per tornare.");
            while (true) {
                KeyStroke k = screen.readInput();
                if (k == null) continue;
                if (k.getKeyType() == KeyType.Enter) break;
                Character ch = k.getCharacter();
                if (ch != null && (ch == 'r' || ch == 'R')) {
                    openReviewsMenu(r.id); // ★
                    break;
                }
            }

        } catch (Exception ignored) {}
    }

    //Modello di Row e metodi
    private static class Row {

        String id, adminId, name, address, location, price, cuisine, longitude, latitude;
        boolean delivery, online;
    }

    private List<Row> loadRestaurants() throws IOException {

        CsvService.CsvTable t = CsvService.read(Paths.get(REST_CSV), true);
        List<Row> out = new ArrayList<>();
        if (t.getRows().isEmpty()) return out;

        int idxId       = col(t, "ID", 0);
        int idxAdmin    = col(t, "ID-Admin", 1);
        int idxName     = col(t, "Name", 2);
        int idxAddress  = col(t, "Address", 3);
        int idxLocation = col(t, "Location", 4);
        int idxPrice    = col(t, "Price", 5);
        int idxCuisine  = col(t, "Cuisine", 6);
        int idxLon      = col(t, "Longitude", 7);
        int idxLat      = col(t, "Latitude", 8);
        int idxDel      = col(t, "delivery", 9);
        int idxOnl      = col(t, "online", 10);

        for (List<String> c : t.getRows()) {
            if (c.size() < 11) continue;

            Row r = new Row();
            r.id        = Helpers.safe(c.get(idxId));
            r.adminId   = Helpers.safe(c.get(idxAdmin));
            r.name      = Helpers.safe(c.get(idxName));
            r.address   = Helpers.safe(c.get(idxAddress));
            r.location  = Helpers.safe(c.get(idxLocation));
            r.price     = Helpers.safe(c.get(idxPrice));
            r.cuisine   = Helpers.safe(c.get(idxCuisine));
            r.longitude = Helpers.safe(c.get(idxLon));
            r.latitude  = Helpers. safe(c.get(idxLat));
            r.delivery  = isYes(c.get(idxDel));
            r.online    = isYes(c.get(idxOnl));
            out.add(r);
        }

        return out;
    }

    //Calcola la media della stelle 
    private Map<String, Double> loadAverageStars() throws IOException {

        CsvService.CsvTable t = CsvService.read(Paths.get(FEED_CSV), true);
        Map<String, int[]> acc = new HashMap<>(); // MIC -> [sum, count]
        Map<String, Double> out = new HashMap<>();
        if (t.getRows().isEmpty()) return out;

        int idxMic   = col(t, "ID-Restaurant", 2);
        int idxStars = col(t, "Stars", 3);

        for (List<String> c : t.getRows()) {

            if (c.size() <= Math.max(idxMic, idxStars)) continue;
            String mic = Helpers.safe(c.get(idxMic));
            Integer stars = Helpers.parseIntOrNull(Helpers.safe(c.get(idxStars)));
            if (mic.isEmpty() || stars == null) continue;

            int[] a = acc.computeIfAbsent(mic, k -> new int[2]);
            a[0] += stars;
            a[1] += 1;
        }

        for (var e : acc.entrySet()) {

            int sum = e.getValue()[0], cnt = e.getValue()[1];
            if (cnt > 0) out.put(e.getKey(), sum / (double) cnt);
        }
        return out;
    }

    //Metodo per apertura del menu recensioni
    private void openReviewsMenu(String restaurantMicId) throws IOException {

        // Provo a identificare l'utente per abilitare azioni (altrimenti sola lettura)
        String email = input.readLine("Inserisci la tua email (invio per solo lettura): ").trim();
        Users u = null;
        Role role = Role.GUEST;

        if (!email.isBlank()) {

            // prova a caricare utente da qualsiasi ruolo
            Users found = AuthService.loadUserByEmail(email);
            if (found != null) {
                u = found;
                role = found.getRole(); // CLIENT o ADMIN
            } else {
                uiMenu.showMessage("Utente non trovato: apertura in sola lettura.");
            }
        }

        // Apri menu recensioni
        FeedbackController fc = new FeedbackController(terminal, screen);
        fc.openMenuForRestaurant(restaurantMicId, u, role);
        TerminalManager.clearScreen();
    }

    //Helpers vari

    private Boolean readTriState(String prompt) throws IOException {

        while (true) {
            String s = input.readLine(prompt).trim().toLowerCase();
            if (s.isEmpty()) return null; // qualsiasi
            if (s.equals("s") || s.equals("si") || s.equals("sì") || s.equals("y") || s.equals("yes")) return true;
            if (s.equals("n") || s.equals("no")) return false;
            uiMenu.showMessage("Risposta non valida. Usa s/n o invio per qualsiasi.");
        }
    }

    private void waitEnter() throws IOException {

        while (true) {
            KeyStroke k = screen.readInput();
            if (k != null && k.getKeyType() == KeyType.Enter) break;
        }
    }

    //
    private static boolean containsIgnoreCase(String hay, String needle) {

        if (hay == null) return false;
        if (needle == null || needle.isBlank()) return true;
        return hay.toLowerCase().contains(needle.toLowerCase());
    }

    private static String yesNo(boolean b) { return b ? "sì" : "no"; }

    private static boolean isYes(String s) {

        if (s == null) return false;
        s = s.trim().toLowerCase();
        return s.equals("sì") || s.equals("si") || s.equals("y") || s.equals("yes");
    }

    /** €,€€,€€€,€€€€ -> 20,40,60,80 ; oppure 20/40/60/80 testuali; altrimenti null */
    private static Integer parsePrice(String raw) {

        if (raw == null || raw.isBlank()) return null;
        raw = raw.trim();
        if (raw.equals("€")) return 20;
        if (raw.equals("€€")) return 40;
        if (raw.equals("€€€")) return 60;
        if (raw.equals("€€€€")) return 80;
        try {
            int v = Integer.parseInt(raw);
            return (v == 20 || v == 40 || v == 60 || v == 80) ? v : null;
        } catch (Exception ignored) { return null; }
    }

    /** Converte il campo prezzo del CSV in 20/40/60/80; se non riconosciuto, null. */
    private static Integer priceToValue(String price) { return parsePrice(price); }

    /** Indice colonna dall'header se presente, altrimenti fallback. */
    private static int col(CsvService.CsvTable t, String name, int fallback) {

        int idx = t.columnIndex(name);
        return (idx >= 0) ? idx : fallback;
    }

    /** Cerca la prima colonna presente tra più nomi possibili, altrimenti fallback. */
    private static int colAny(CsvService.CsvTable t, List<String> names, int fallback) {
        
        if (names != null) {
            for (String n : names) {
                int i = t.columnIndex(n);
                if (i >= 0) return i;
            }
        }
        return fallback;
    }
}
