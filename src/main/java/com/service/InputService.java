package com.service;

import java.io.IOException;
import java.util.List;

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

        // -------------------- Input helpers (Lanterna/Screen) --------------------
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
}
