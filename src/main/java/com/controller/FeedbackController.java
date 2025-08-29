package com.controller;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;

import com.model.*;
import com.view.UIMenu;
import com.service.*;

import java.io.IOException;
import java.util.List;
/* 
/**
 * Controller Lanterna per la parte Feedback (recensioni).
 * - Mantiene separazione da RestaurantController.
 * - Usa RestaurantService per la logica.


public class FeedbackController{

    private final Terminal terminal;
    private final Screen screen;
    private final UIMenu uiMenu;
    private final RestaurantService service = RestaurantService.getInstance();

    public FeedbackController(Terminal terminal, Screen screen){
        this.terminal = terminal;
        this.screen = screen;
        this.uiMenu = new UIMenu(terminal, screen);    
    }

    // -------------------- FLOW: AGGIUNGI RECENSIONE --------------------
    public void addReviewFlow(int restaurantId, Users currentUser) throws IOException {
        TerminalManager.clearScreen();
        uiMenu.drawMenu(List.of(""), 0, "AGGIUNGI RECENSIONE");

        Integer stars = parseIntOrNull(readLine("Stelle [1..5]: "));
        if (stars == null || stars < 1 || stars > 5) {
            uiMenu.showMessage("Valore stelle non valido. Operazione annullata.");
            terminal.flush();
            waitEnter();
            return;
        }
        String text = readLine("Commento: ");
        service.addReview(restaurantId, currentUser, stars, text);

        uiMenu.showMessage("Recensione aggiunta.");
        uiMenu.showMessage("Premi Invio per tornare...");
        terminal.flush();
        waitEnter();
    }

    // -------------------- FLOW: MODIFICA RECENSIONE --------------------
    public void editReviewFlow(int reviewId, Users currentUser) throws IOException {
        TerminalManager.clearScreen();
        uiMenu.drawMenu(List.of(""), 0, "MODIFICA RECENSIONE");

        Integer stars = parseIntOrNull(readLine("Nuove stelle [1..5] (invio per lasciare invariato): "));
        String text = readLine("Nuovo commento (invio per lasciare invariato): ");
        service.editReview(reviewId, currentUser,
                stars,
                (text == null || text.isBlank()) ? null : text);

        uiMenu.showMessage("Recensione modificata (se autorizzato).");
        uiMenu.showMessage("Premi Invio per tornare...");
        terminal.flush();
        waitEnter();
    }

    // -------------------- FLOW: ELIMINA RECENSIONE --------------------
    public void deleteReviewFlow(int reviewId, Users currentUser) throws IOException {
        TerminalManager.clearScreen();
        uiMenu.drawMenu(List.of(""), 0, "ELIMINA RECENSIONE");
        boolean ok = false;
        try {
            ok = service.deleteReview(reviewId, currentUser);
        } catch (Exception e) {
            uiMenu.showMessage("Errore: " + e.getMessage());
        }

        uiMenu.showMessage(ok ? "Feedback eliminato." : "Feedback non trovato.");
        uiMenu.showMessage("Premi Invio per tornare...");
        terminal.flush();
        waitEnter();
    }

    // -------------------- FLOW: RISPOSTA DEL PROPRIETARIO --------------------
    public void replyReviewFlow(int reviewId, Users owner) throws IOException {
        TerminalManager.clearScreen();
        uiMenu.drawMenu(List.of(""), 0, "RISPONDI A RECENSIONE");

        String reply = readLine("Risposta: ");
        try {
            service.replyToReview(reviewId, owner, reply);
            uiMenu.showMessage("Risposta inviata.");
        } catch (Exception e) {
            uiMenu.showMessage("Errore: " + e.getMessage());
        }

        uiMenu.showMessage("Premi Invio per tornare...");
        terminal.flush();
        waitEnter();
    }

    // -------------------- UTIL INPUT --------------------
    private String readLine(String prompt) throws IOException {
        uiMenu.redrawInputLine(prompt, "");
        StringBuilder sb = new StringBuilder();
        while (true) {
            KeyStroke key = screen.readInput();
            if (key == null) continue;
            KeyType kt = key.getKeyType();
            if (kt == KeyType.Enter) break;
            if (kt == KeyType.Backspace && sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            } else if (key.getCharacter() != null && !Character.isISOControl(key.getCharacter())) {
                sb.append(key.getCharacter());
            }
            uiMenu.redrawInputLine(prompt, sb.toString());
        }
        return sb.toString().trim();
    }

    private void waitEnter() throws IOException {
        while (true) {
            KeyStroke k = screen.readInput();
            if (k != null && k.getKeyType() == KeyType.Enter) break;
        }
    }

    private Integer parseIntOrNull(String s) {
        try { return (s == null || s.isBlank()) ? null : Integer.parseInt(s.trim()); }
        catch (Exception e) { return null; }
    }
}
*/
