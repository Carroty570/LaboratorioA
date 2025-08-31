package com.view;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class UIMenu {

    private final Screen screen;

    // Stato per gestione banner/resize
    private boolean bannerDrawn = false;
    private int lastCols = -1;
    private int lastRows = -1;
    private int bannerTopRow = 2;     // riga di partenza banner
    private int bannerBottomRow = 2;  // ultima riga occupata dal banner (inclusa)

    public UIMenu(com.googlecode.lanterna.terminal.Terminal terminal, Screen screen) {

        this.screen = screen;
    }

    //Stampa banner centrata e allineata
    public void printBanner() {

        try {
            TerminalSize size = screen.getTerminalSize();
            int cols = size.getColumns();
            int rows = size.getRows();

            // Se dimensione terminale è cambiata, forziamo un nuovo draw del banner
            boolean resized = (cols != lastCols || rows != lastRows);
            if (resized) {
                bannerDrawn = false;
                lastCols = cols;
                lastRows = rows;
            }
            if (bannerDrawn) return;

            TextGraphics tg = screen.newTextGraphics();
            tg.setForegroundColor(TextColor.ANSI.CYAN);
            tg.enableModifiers(SGR.BOLD);

            int row = Math.max(1, (int) Math.floor(rows * 0.05)); // banner top ~5% dall'alto
            bannerTopRow = row;

            try (BufferedReader reader = new BufferedReader(

                    new InputStreamReader(
                            getClass().getResourceAsStream("/banner.txt")
                    ))) {
                String line;
                while (reader != null && (line = reader.readLine()) != null) {

                    int lineLength = line.length();
                    int col = Math.max(0, (cols - lineLength) / 2); // centratura orizzontale
                    tg.putString(col, row++, line, SGR.BOLD);
                }
                bannerBottomRow = row - 1;
            } catch (Exception e) {

                // fallback minimale se risorsa assente
                tg.putString(2, 1, "=== WELCOME TO THE KNIFE ===", SGR.BOLD);
                bannerTopRow = 1;
                bannerBottomRow = 1;
            }

            tg.disableModifiers(SGR.BOLD);
            screen.refresh();
            bannerDrawn = true;

        } catch (Exception ignored) {

            // in caso di errore, non bloccare il resto dell'UI
        }
    }

    //Disegno del menu 
    public void drawMenu(List<String> opzioni, int selezione, String titolo) {
        
        try {

            printBanner();

            TextGraphics tg = screen.newTextGraphics();
            TerminalSize size = screen.getTerminalSize();
            int cols = size.getColumns();
            int rows = size.getRows();

            // Calcolo area del menu in percentuale
            // Larghezza ~70% dello schermo, con min/max
            int boxWidth = clamp((int) Math.round(cols * 0.70), 40, Math.max(20, cols - 4));
            int left = Math.max(2, (cols - boxWidth) / 2);

            // Top ~ 25% dell'altezza o appena sotto il banner, scegli il più basso (per non sovrapporre)
            int topPercent = (int) Math.floor(rows * 0.25);
            int top = Math.max(bannerBottomRow + 2, topPercent);
            int startRow = top + 3; // spazio per intestazione cornice

            // Righe disponibili da top al fondo
            int availableRows = rows - top - 4; // margine inferiore
            if (availableRows < 6) {
                // estremo: schermo molto piccolo → adatta comunque
                availableRows = Math.max(4, rows - top - 2);
            }

            // Pulizia area menu (solo il rettangolo interessato)
            int clearHeight = Math.min(availableRows, opzioni.size() + 6); // +5/+6 per cornici e hint
            tg.setForegroundColor(TextColor.ANSI.DEFAULT);
            tg.fillRectangle(
                    new com.googlecode.lanterna.TerminalPosition(left, top),
                    new TerminalSize(boxWidth, Math.max(5, clearHeight)),
                    ' '
            );

            // Titolo e cornice superiore
            tg.setForegroundColor(TextColor.ANSI.CYAN);
            tg.enableModifiers(SGR.BOLD);
            tg.putString(left, top,      "╔" + "═".repeat(Math.max(0, boxWidth - 2)) + "╗");
            tg.putString(left, top + 1,  "║" + centerText(titolo, boxWidth - 2) + "║");
            tg.putString(left, top + 2,  "╠" + "═".repeat(Math.max(0, boxWidth - 2)) + "╣");
            tg.disableModifiers(SGR.BOLD);

            // Opzioni che posso mostrare
            int roomForOptions = availableRows - 5; // 3 righe header + 1 riga bottom + 1 hint
            if (roomForOptions < 1) roomForOptions = 1;

            int shown = Math.min(opzioni.size(), roomForOptions);

            // Se non c'è spazio per tutte, facciamo "finestra" centrata sul selezionato
            int windowStart = 0;
            if (shown < opzioni.size()) {
                // teniamo la selezione entro la finestra
                int half = shown / 2;
                windowStart = Math.max(0, selezione - half);
                if (windowStart + shown > opzioni.size()) {
                    windowStart = opzioni.size() - shown;
                }
            }

            // Disegno opzioni (con freccia per selezione)
            for (int i = 0; i < shown; i++) {
                int idx = windowStart + i;
                boolean sel = (idx == selezione);
                String freccia = sel ? "-> " : "   ";
                String label = freccia + opzioni.get(idx);

                tg.setForegroundColor(TextColor.ANSI.CYAN);
                if (sel) tg.enableModifiers(SGR.BOLD, SGR.REVERSE);
                tg.putString(left, startRow + i, "║ " + padRight(label, boxWidth - 4) + " ║");
                if (sel) tg.disableModifiers(SGR.BOLD, SGR.REVERSE);
            }

            // Se abbiamo tagliato in alto o in basso, mostra indicatori
            int bottomRow = startRow + shown;
            tg.setForegroundColor(TextColor.ANSI.CYAN);
            tg.putString(left, bottomRow, "╚" + "═".repeat(Math.max(0, boxWidth - 2)) + "╝");

            if (windowStart > 0) {
                tg.putString(left + boxWidth - 3, top + 2, "↑");          // indicatore "c'è sopra"
            }
            if (windowStart + shown < opzioni.size()) {
                tg.putString(left + boxWidth - 3, bottomRow, "↓");        // indicatore "c'è sotto"
            }

            // Hint (posizionato appena sotto la cornice)
            int hintRow = Math.min(rows - 2, bottomRow + 2);
            tg.putString(left, hintRow, "Usa ↑/↓ o W/S, INVIO per selezionare");

            screen.refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Mostra un messaggio che si adatta al menu e al terminale, viene stampato in basso
    public void showMessage(String msg) {

        try {

            TextGraphics tg = screen.newTextGraphics();
            TerminalSize size = screen.getTerminalSize();
            int cols = size.getColumns();
            int row = Math.max(1, size.getRows() - 2);
            tg.setForegroundColor(TextColor.ANSI.CYAN);
            tg.putString(2, row, " ".repeat(Math.max(0, cols - 4)));
            tg.putString(2, row, truncate(msg, cols - 4));
            screen.refresh();
        } catch (Exception ignored) {}
    }

    public void redrawInputLine(String prompt, String input) {

        try {

            TextGraphics tg = screen.newTextGraphics();
            TerminalSize size = screen.getTerminalSize();
            int cols = size.getColumns();
            int row = Math.max(1, size.getRows() - 4);
            tg.setForegroundColor(TextColor.ANSI.WHITE);
            tg.putString(2, row, " ".repeat(Math.max(0, cols - 4)));
            String line = (prompt == null ? "" : prompt) + (input == null ? "" : input);
            tg.putString(2, row, truncate(line, cols - 4));
            screen.refresh();
        } catch (Exception ignored) {}
    }

    //Vari helpers per disegnare i menu nella maniera corretta
    
    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    private String centerText(String text, int width) {
        if (text == null) text = "";
        if (width <= 0) return "";
        if (text.length() >= width) return text.substring(0, width);
        int pad = (width - text.length()) / 2;
        return " ".repeat(pad) + text + " ".repeat(width - text.length() - pad);
    }

    private String padRight(String text, int width) {
        if (text == null) text = "";
        if (width <= 0) return "";
        if (text.length() >= width) return text.substring(0, width);
        return text + " ".repeat(width - text.length());
    }

    private String truncate(String s, int maxLen) {
        if (s == null) return "";
        if (maxLen <= 0) return "";
        return (s.length() <= maxLen) ? s : s.substring(0, maxLen);
    }
}
