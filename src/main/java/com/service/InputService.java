package com.service;

import java.io.IOException;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import com.view.UIMenu;

public class InputService {

    private final Screen screen;
    private final UIMenu uiMenu;

    public InputService(Terminal terminal, Screen screen){

        this.screen = screen;
        this.uiMenu = new UIMenu(terminal, screen);
    }

    //Input helpers (Lanterna/Screen)
    
    public String readLine(String prompt) throws IOException {

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

    public String readPassword(String prompt) throws IOException {

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
            uiMenu.redrawInputLine(prompt, "*".repeat(sb.length()));
        }
        return sb.toString();
    }

    public Integer readStars(String prompt) throws IOException {

        while (true) {
            String s = readLine(prompt);
            if (s == null || s.isBlank()) {
                uiMenu.showMessage("Operazione annullata.");
                waitEnter();
                return null;
            }
            try {
                int v = Integer.parseInt(s.trim());
                if (v >= 1 && v <= 5) return v;
            } catch (Exception ignored) {}
            uiMenu.showMessage("Valore non valido. Inserisci un numero da 1 a 5.");
        }
    }

    public void waitEnter() throws IOException {

        while (true) {
            KeyStroke k = screen.readInput();
            if (k != null && k.getKeyType() == KeyType.Enter) break;
        }
    }
}
