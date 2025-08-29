package com.controller;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;

import com.model.*;
import com.view.UIMenu;
import com.service.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class RestaurantController {

    private final UIMenu uiMenu;
    private final InputService input;
    private final Screen screen;

    private static final String CSV_PATH = "data/restaurants.csv";

    public RestaurantController(Terminal terminal, Screen screen) {

        this.uiMenu = new UIMenu(terminal, screen);
        this.input = new InputService(terminal, screen);
        this.screen = screen;
    }

    /** Aggiunge un nuovo ristorante e lo salva nel CSV */
    public Restaurant addRestaurant(Role role, List<String> opzioni, int selezione) {

        try {
            TerminalManager.clearScreen();
            uiMenu.drawMenu(opzioni, selezione, "AGGIUNGI RISTORANTE");

            String name = input.readLine("Nome: ");
            String address = input.readLine("Indirizzo: ");
            String location = input.readLine("Località: ");
            String price = input.readLine("Prezzo medio (es. €€): ");
            String cuisine = input.readLine("Cucina: ");
            String longitude = input.readLine("Longitudine: ");
            String latitude = input.readLine("Latitudine: ");
            boolean delivery = readYesNo("Delivery disponibile (s/n)? ");
            boolean online = readYesNo("Prenotazione online (s/n)? ");

            // Calcola nuovo ID
            String newId = nextId();

            // Appendi al CSV
            appendCsvRow(newId, name, address, location, price, cuisine,
                    longitude, latitude, delivery, online);

            // Costruisci anche oggetto Restaurant (solo name/address + flags per compatibilità col model)
            Restaurant r = new Restaurant(name, address);
            r.setDelivery(delivery);
            r.setOnlineReservation(online);

            uiMenu.showMessage("\nRistorante aggiunto con ID: " + newId);
            uiMenu.showMessage("Premi Invio per continuare...");
            waitEnter();

            return r;
        } catch (Exception e) {
            throw new RuntimeException("Errore durante addRestaurant", e);
        }
    }

    /** Elimina un ristorante dal CSV cercandolo per ID */
    public Restaurant delRestaurant(Role role, List<String> opzioni, int selezione) {

        try {
            TerminalManager.clearScreen();
            uiMenu.drawMenu(opzioni, selezione, "ELIMINA RISTORANTE");

            String id = input.readLine("Inserisci ID ristorante (es. MIC-000123): ");
            List<String> lines = Files.readAllLines(Paths.get(CSV_PATH), StandardCharsets.UTF_8);

            if (lines.size() <= 1) {
                uiMenu.showMessage("Nessun ristorante presente.");
                waitEnter();
                return null;
            }

            String header = lines.get(0);
            List<String> newLines = new ArrayList<>();
            newLines.add(header);

            boolean removed = false;
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.startsWith(id + ",")) {
                    removed = true;
                } else {
                    newLines.add(line);
                }
            }

            if (removed) {
                Files.write(Paths.get(CSV_PATH), newLines, StandardCharsets.UTF_8);
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

    /** Mostra i dettagli di un ristorante dal CSV */
    public Restaurant viewRestaurantDetails(Role role, List<String> opzioni, int selezione) {

        try {

            TerminalManager.clearScreen();
            uiMenu.drawMenu(opzioni, selezione, "DETTAGLI RISTORANTE");
            List<String> lines = Files.readAllLines(Paths.get(CSV_PATH), StandardCharsets.UTF_8);
            String id = input.readLine("Inserisci ID ristorante (es. MIC-000123): ");


            for (int i = 1; i < lines.size(); i++) {
                String[] cols = lines.get(i).split(",", -1);
                if (cols[0].equals(id)) {
                    uiMenu.showMessage("\nID: " + cols[0] + "Nome: " + cols[1] + "Indirizzo: " + cols[2] + "Località: " + cols[3]
                    + "Prezzo: " + cols[4] + "Cucina: " + cols[5] + "Longitudine: " + cols[6] + "Latitudine: " + cols[7] + "Delivery: " + cols[8] + "Online: " + cols[9]);
                    
                    /*uiMenu.showMessage("Nome: " + cols[1]);
                    uiMenu.showMessage("Indirizzo: " + cols[2]);
                    uiMenu.showMessage("Località: " + cols[3]);
                    uiMenu.showMessage("Prezzo: " + cols[4]);
                    uiMenu.showMessage("Cucina: " + cols[5]);
                    uiMenu.showMessage("Longitudine: " + cols[6]);
                    uiMenu.showMessage("Latitudine: " + cols[7]);
                    uiMenu.showMessage("Delivery: " + cols[8]);
                    uiMenu.showMessage("Online: " + cols[9]);*/

                    waitEnter();
                    return null;
                }
            }

            uiMenu.showMessage("Ristorante non trovato con ID " + id);
            waitEnter();
            return null;

        } catch (Exception e) {
            throw new RuntimeException("Errore durante viewRestaurantDetails", e);
        }
    }

    // ========================
    // Supporto CSV / ID
    // ========================

    private void appendCsvRow(String id, String name, String address, String location,
                              String price, String cuisine,
                              String longitude, String latitude,
                              boolean delivery, boolean online) throws IOException {
        File file = new File(CSV_PATH);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(
                    new FileOutputStream(file, false), StandardCharsets.UTF_8))) {
                pw.println("ID,Name,Address,Location,Price,Cuisine,Longitude,Latitude,delivery,online");
            }
        }
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(file, true), StandardCharsets.UTF_8))) {
            pw.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                    id,
                    safe(name),
                    safe(address),
                    safe(location),
                    safe(price),
                    safe(cuisine),
                    safe(longitude),
                    safe(latitude),
                    delivery ? "sì" : "no",
                    online ? "sì" : "no");
        }
    }

    private String nextId() throws IOException {

        File file = new File(CSV_PATH);
        if (!file.exists()) return "MIC-000001";

        List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        if (lines.size() <= 1) return "MIC-000001";

        String last = lines.get(lines.size() - 1);
        String[] cols = last.split(",", -1);
        String lastId = cols[0];
        int num = Integer.parseInt(lastId.substring(4));
        return String.format("MIC-%06d", num + 1);
    }

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

    private static String safe(String s) {

        return (s == null) ? "" : s.replace(",", " ").trim();
    }
}
