package com.view;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class UIMenu {

    private final Screen screen;
    private boolean bannerDrawn = false; // <— evita di ristampare


    public UIMenu(Terminal terminal, Screen screen) {
        this.screen = screen;
    }

    // Banner (file resources/banner.txt)
    public void printBanner() {

        if (bannerDrawn) return;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        // usa lo slash iniziale e verifica null
                        getClass().getResourceAsStream("/banner.txt")
                ))) {

            TextGraphics tg = screen.newTextGraphics();

            int row = 2;
            int screenWidth = screen.getTerminalSize().getColumns();

            tg.setForegroundColor(TextColor.ANSI.CYAN);
            tg.enableModifiers(SGR.BOLD);

            String line;
            while (reader != null && (line = reader.readLine()) != null) {

                int lineLength = line.length();
                int col = Math.max(0, (screenWidth - lineLength) / 2); // centratura orizzontale
                tg.putString(col, row++, line, SGR.BOLD);
            }

            tg.disableModifiers(SGR.BOLD);
            screen.refresh();

        } catch (Exception e) {
            // fallback minimale se risorsa assente
            try {
                TextGraphics tg = screen.newTextGraphics();
                tg.setForegroundColor(TextColor.ANSI.CYAN);
                tg.enableModifiers(SGR.BOLD);
                tg.putString(2, 1, "=== WELCOME TO THE KNIFE ===");
                tg.disableModifiers(SGR.BOLD);
                screen.refresh();
            } catch (Exception ignore) {}
        }
    }

    // Disegna un menu con cornice ed evidenziazione elemento selezionato
    public void drawMenu(List<String> opzioni, int selezione, String titolo) {

        try {
            // Assicurati che il banner sia presente
            printBanner();

            TextGraphics tg = screen.newTextGraphics();
            TerminalSize size = screen.getTerminalSize();

            // Dimensioni/posizione del box menu (sotto il banner)
            int boxWidth = Math.min(60, size.getColumns() - 6);
            int left = Math.max(3, (size.getColumns() - boxWidth) / 2);
            int top = 12; // <— lascia spazio al banner
            int startRow = top + 3;

            // PULISCI SOLO L’AREA DEL MENU (non tutta la screen)
            tg.setForegroundColor(TextColor.ANSI.DEFAULT);
            tg.fillRectangle(new com.googlecode.lanterna.TerminalPosition(left, top),
                    new TerminalSize(boxWidth, opzioni.size() + 5), ' ');

            // titolo e cornice
            tg.setForegroundColor(TextColor.ANSI.CYAN);
            tg.enableModifiers(SGR.BOLD);
            tg.putString(left, top,      "╔" + "═".repeat(boxWidth - 2) + "╗");
            tg.putString(left, top + 1,  "║" + centerText(titolo, boxWidth - 2) + "║");
            tg.putString(left, top + 2,  "╠" + "═".repeat(boxWidth - 2) + "╣");
            tg.disableModifiers(SGR.BOLD);

            // opzioni
            for (int i = 0; i < opzioni.size(); i++) {
                boolean sel = (i == selezione);
                String freccia = sel ? "-> " : "  ";
                String label = freccia + opzioni.get(i);

                tg.setForegroundColor(sel ? TextColor.ANSI.CYAN : TextColor.ANSI.CYAN);
                if (sel) tg.enableModifiers(SGR.BOLD, SGR.REVERSE);
                tg.putString(left, startRow + i, "║ " + padRight(label, boxWidth - 4) + " ║");
                if (sel) tg.disableModifiers(SGR.BOLD, SGR.REVERSE);
            }

            // bordino inferiore
            tg.setForegroundColor(TextColor.ANSI.CYAN);
            tg.putString(left, startRow + opzioni.size(), "╚" + "═".repeat(boxWidth - 2) + "╝");

            // hint
            tg.setForegroundColor(TextColor.ANSI.CYAN);
            tg.putString(left, startRow + opzioni.size() + 2, "Usa ↑/↓ o W/S, INVIO per selezionare");

            screen.refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // messaggio in basso
    public void showMessage(String msg) {

        try {
            TextGraphics tg = screen.newTextGraphics();
            TerminalSize size = screen.getTerminalSize();
            int row = size.getRows() - 2;
            tg.setForegroundColor(TextColor.ANSI.CYAN);
            tg.putString(2, row, " ".repeat(Math.max(0, size.getColumns() - 4)));
            tg.putString(2, row, msg);
            screen.refresh();
        } catch (Exception ignored) {}
    }

    // ridisegna la riga di input (prompt + testo)
    public void redrawInputLine(String prompt, String input) {

        try {
            TextGraphics tg = screen.newTextGraphics();
            TerminalSize size = screen.getTerminalSize();
            int row = size.getRows() - 4;
            tg.setForegroundColor(TextColor.ANSI.WHITE);
            tg.putString(2, row, " ".repeat(Math.max(0, size.getColumns() - 4)));
            tg.putString(2, row, prompt + (input == null ? "" : input));
            screen.refresh();
        } catch (Exception ignored) {}
    }

    //Centra qualsiasi riga di testo
    private String centerText(String text, int width) {

        if (text.length() >= width) return text.substring(0, width);
        int pad = (width - text.length()) / 2;
        return " ".repeat(pad) + text + " ".repeat(width - text.length() - pad);
    }

    
    private String padRight(String text, int width) {

        if (text.length() >= width) return text.substring(0, width);
        return text + " ".repeat(width - text.length());
    }
}
