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
import com.service.*;
import com.view.UIMenu;
import com.utils.Helpers;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FeedbackController {

    private final Screen screen;
    private final UIMenu uiMenu;
    private final Helpers help;
    private final InputService input;

    public FeedbackController(Terminal terminal, Screen screen){

        this.screen = screen;
        this.uiMenu = new UIMenu(terminal, screen);
        this.help = new Helpers();
        this.input = new InputService(terminal, screen);
    }

    
    //Apre il "menu recensioni" per un ristorante specifico.
    public void openMenuForRestaurant(String restaurantMicId, Users currentUser, Role role) throws IOException {

        while (true) {

            TerminalManager.clearScreen();
            String titolo = "RECENSIONI — " + restaurantMicId + Helpers.avgSuffix(restaurantMicId);
            List<String> opzioni;

            if (role == Role.ADMIN) {

                opzioni = List.of(
                        "Vedi tutte le recensioni",
                        "Rispondi a una recensione",
                        "Torna indietro"
                );

            } else { // CLIENT (o guest -> trattato come client ma richiede login su azioni personali)

                opzioni = List.of(

                        "Aggiungi/Modifica la mia recensione",
                        "Elimina la mia recensione",
                        "Vedi tutte le recensioni",
                        "Torna indietro"
                );
            }

            int sel = interactiveMenu(opzioni, titolo);
            String scelta = opzioni.get(sel).toLowerCase();

            switch (scelta) {

                case "vedi tutte le recensioni" -> showAllReviewsFlow(restaurantMicId);

                case "aggiungi/modifica la mia recensione" -> {
                    if (!ensureLogged(currentUser)) break;
                    addOrEditMyReviewFlow(restaurantMicId, currentUser);
                }

                case "elimina la mia recensione" -> {
                    if (!ensureLogged(currentUser)) break;
                    deleteMyReviewFlow(restaurantMicId, currentUser);
                }

                case "rispondi a una recensione" -> {
                    if (role != Role.ADMIN) {
                        uiMenu.showMessage("Solo l'admin può rispondere alle recensioni.");
                        input.waitEnter();
                        break;
                    }
                    replyToReviewFlow(restaurantMicId, currentUser);
                }

                case "torna indietro" -> {
                    TerminalManager.clearScreen();
                    return;
                }
            }
        }
    }

    
    // FLOWS CLIENT

    private void addOrEditMyReviewFlow(String restaurantMicId, Users currentUser) throws IOException {

        TerminalManager.clearScreen();
        uiMenu.drawMenu(List.of(""), 0, "AGGIUNGI / MODIFICA RECENSIONE");

        String email = Helpers.safe(currentUser == null ? null : currentUser.getEmail());

        if (email.isEmpty()) {

            uiMenu.showMessage("Utente non loggato.");
            input.waitEnter();
            return;
        }

        Optional<Feedback> mine = FeedbackService.getMyReview(restaurantMicId, email);
        mine.ifPresent(f -> uiMenu.showMessage("Trovata recensione esistente: " + f.getStars() + "★ — " + Helpers.truncate(f.getComment(), 40)));

        Integer stars = input.readStars("Stelle [1..5]: ");
        if (stars == null) return;
        String text = input.readLine("Commento: ");

        try {

            String id = FeedbackService.upsertReview(restaurantMicId, email, stars, text);
            uiMenu.showMessage("Recensione salvata (ID: " + id + ").");
        } catch (Exception e) {

            uiMenu.showMessage("Errore: " + e.getMessage());
        }

        uiMenu.showMessage("Premi Invio per tornare...");
        input.waitEnter();
    }

    private void deleteMyReviewFlow(String restaurantMicId, Users currentUser) throws IOException {

        TerminalManager.clearScreen();
        uiMenu.drawMenu(List.of(""), 0, "ELIMINA LA MIA RECENSIONE");

        String email = Helpers.safe(currentUser == null ? null : currentUser.getEmail());
        if (email.isEmpty()) {

            uiMenu.showMessage("Utente non loggato.");
            input.waitEnter();
            return;
        }

        boolean ok = FeedbackService.deleteMyReview(restaurantMicId, email);
        uiMenu.showMessage(ok ? "Recensione eliminata." : "Nessuna recensione trovata da eliminare.");
        uiMenu.showMessage("Premi Invio per tornare...");
        input.waitEnter();
    }

    // FLOWS ADMIN

    private void replyToReviewFlow(String restaurantMicId, Users owner) throws IOException {

        TerminalManager.clearScreen();
        uiMenu.drawMenu(List.of(""), 0, "RISPOSTA A RECENSIONE");

        List<Feedback> list = FeedbackService.getReviewsByRestaurant(restaurantMicId);

        if (list.isEmpty()) {

            uiMenu.showMessage("Nessuna recensione presente.");
            input.waitEnter();
            return;
        }

        Integer idx = pickReviewIndex(list, restaurantMicId);
        if (idx == null) return;

        Feedback f = list.get(idx);
        String reply = input.readLine("Risposta: ");

        try {

            FeedbackService.replyToFeedback(f.getIdFeedback(), Helpers.safe(owner == null ? null : owner.getEmail()), reply);
            uiMenu.showMessage("Risposta inviata per ID " + f.getIdFeedback() + ".");
        } catch (Exception e) {

            uiMenu.showMessage("Errore: " + e.getMessage());
        }

        uiMenu.showMessage("Premi Invio per tornare...");
        input.waitEnter();
    }

    //Lista recensioni comune

    private void showAllReviewsFlow(String restaurantMicId) throws IOException {

        TerminalManager.clearScreen();
        uiMenu.drawMenu(List.of(""), 0, "TUTTE LE RECENSIONI");

        List<Feedback> list = FeedbackService.getReviewsByRestaurant(restaurantMicId);
        Map<String,String> replies = FeedbackService.repliesByRestaurant(restaurantMicId); // se non esiste file -> mappa vuota

        if (list.isEmpty()) {
            uiMenu.showMessage("Nessuna recensione presente.");
            input.waitEnter();
            return;
        }

        final int PAGE = 20;
        int page = 0;

        while (true) {
        
            renderReviewsPage(list, replies, page, PAGE, restaurantMicId);
            String cmd = input.readLine("Comandi: A avanti | D indietro | Q esci: ").trim();

            if (cmd.equalsIgnoreCase("Q")) return;
            if (cmd.equalsIgnoreCase("A")) {
                int maxPage = (list.size() - 1) / PAGE;
                if (page < maxPage) page++;
                continue;
            }
            if (cmd.equalsIgnoreCase("D")) {
                if (page > 0) page--;
                continue;
            }

            uiMenu.showMessage("Comando non valido.");
        }
    }

    private Integer pickReviewIndex(List<Feedback> list, String restaurantMicId) throws IOException {

        final int PAGE = 20;
        int page = 0;

        while (true) {

            renderReviewsPage(list, Map.of(), page, PAGE, restaurantMicId);
            String cmd = input.readLine("Seleziona [1-" + Math.min(PAGE, list.size() - page * PAGE) + "] / A avanti / D indietro / Q annulla: ").trim();

            if (cmd.equalsIgnoreCase("Q")) return null;

            if (cmd.equalsIgnoreCase("A")) {

                int maxPage = (list.size() - 1) / PAGE;
                if (page < maxPage) page++;
                continue;
            }

            if (cmd.equalsIgnoreCase("D")) {

                if (page > 0) page--;
                continue;
            }

            Integer choice = Helpers.parseIntOrNull(cmd);
            if (choice == null || choice < 1 || choice > PAGE) {

                uiMenu.showMessage("Selezione non valida.");
                continue;
            }

            int idx = page * PAGE + (choice - 1);
            if (idx < 0 || idx >= list.size()) {

                uiMenu.showMessage("Selezione fuori intervallo.");
                continue;
            }

            return idx;
        }
    }

    private void renderReviewsPage(List<Feedback> list, Map<String,String> replies, int page, int pageSize, String micId) {

        try {
            TextGraphics tg = screen.newTextGraphics();
            TerminalSize size = screen.getTerminalSize();
            int cols = size.getColumns();
            int left = 4;
            int top  = Math.max(8, (int)Math.floor(size.getRows()*0.25));

            // pulizia area
            int height = Math.min(28, Math.max(12, size.getRows() - top - 4));
            tg.setForegroundColor(TextColor.ANSI.DEFAULT);
            tg.fillRectangle(new com.googlecode.lanterna.TerminalPosition(left, top),
                    new TerminalSize(Math.max(10, cols - left - 4), height), ' ');

            int start = page * pageSize;
            int end   = Math.min(list.size(), start + pageSize);

            tg.setForegroundColor(TextColor.ANSI.CYAN);
            tg.enableModifiers(SGR.BOLD);
            tg.putString(left, top, String.format("Recensioni %s — Pagina %d/%d — Totale: %d",
                    micId, page + 1, Math.max(1, (list.size() + pageSize - 1) / pageSize), list.size()));
            tg.disableModifiers(SGR.BOLD);

            int row = top + 2;
            for (int i = start; i < end; i++) {
                int num = i - start + 1;
                Feedback f = list.get(i);
                String reply = replies.getOrDefault(f.getIdFeedback(), null);

                tg.putString(left, row++, String.format("%2d) [%s] %d★  %s",
                        num, f.getIdFeedback(), f.getStars(), Helpers.safe(f.getMail())));
                tg.putString(left + 4, row++, Helpers.truncate(f.getComment(), Math.max(20, cols - left - 8)));
                if (reply != null && !reply.isBlank()) {
                    tg.setForegroundColor(TextColor.ANSI.CYAN);
                    tg.putString(left + 4, row++, "Risposta: " + Helpers.truncate(reply, Math.max(20, cols - left - 14)));
                    tg.setForegroundColor(TextColor.ANSI.DEFAULT);
                }

                row++;
            }

            screen.refresh();
        } catch (Exception ignored) {}
    }


    //Menu Utils
    private int interactiveMenu(List<String> opzioni, String titolo) throws IOException {

        int selezione = 0;
        uiMenu.drawMenu(opzioni, selezione, titolo);

        while (true) {

            KeyStroke key = screen.readInput();
            if (key == null) continue;

            KeyType type = key.getKeyType();
            Character ch = key.getCharacter();

            switch (type) {

                case ArrowUp   -> { selezione = (selezione - 1 + opzioni.size()) % opzioni.size(); uiMenu.drawMenu(opzioni, selezione, titolo); }

                case ArrowDown -> { selezione = (selezione + 1) % opzioni.size(); uiMenu.drawMenu(opzioni, selezione, titolo); }

                case Enter     -> { return selezione; }

                default -> {

                    if (ch != null) {
                        if (ch == 'w' || ch == 'W') { selezione = (selezione - 1 + opzioni.size()) % opzioni.size(); uiMenu.drawMenu(opzioni, selezione, titolo); }
                        else if (ch == 's' || ch == 'S') { selezione = (selezione + 1) % opzioni.size(); uiMenu.drawMenu(opzioni, selezione, titolo); }
                    }
                }
            }
        }
    }

    //Utili
    private boolean ensureLogged(Users u) throws IOException {
        if (u == null || Helpers.safe(u.getEmail()).isEmpty()) {
            uiMenu.showMessage("Devi essere loggato per questa operazione.");
            input.waitEnter();
            return false;
        }
        return true;
    }
}
