package com.service;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

// IMPORTANTE: aggiungi questi import per il font
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;

import java.awt.Font;   // <-- AWT Font
import java.io.IOException;

public class TerminalManager {

    private static Terminal terminal;
    private static Screen screen;

    private static void init() {
        if (terminal == null) {
            try {
                // 1) Scegli il font (monospaziato!)
                // Prova con uno di questi: "Consolas", "Cascadia Mono", "Fira Code", "JetBrains Mono", "DejaVu Sans Mono"
                Font baseFont;
                try {
                    baseFont = new Font("Consolas", Font.PLAIN, 18); // <--- cambia qui la dimensione
                    // In alcuni sistemi Windows il font name è case sensitive o non installato
                    if (!baseFont.getFamily().equalsIgnoreCase("Consolas")) {
                        throw new RuntimeException("Font non disponibile, uso Monospaced");
                    }
                } catch (Exception ex) {
                    baseFont = new Font(Font.MONOSPACED, Font.PLAIN, 18);
                }

                // 2) Crea la configurazione font per l’emulatore Swing
                SwingTerminalFontConfiguration fontCfg = SwingTerminalFontConfiguration.newInstance(baseFont);

                // 3) Costruisci il terminale emulato con font e dimensione in celle
                DefaultTerminalFactory factory = new DefaultTerminalFactory()
                        .setInitialTerminalSize(new TerminalSize(140, 40))   // colonne x righe (celle)
                        .setTerminalEmulatorTitle("The Knife")
                        .setTerminalEmulatorFontConfiguration(fontCfg)       // <-- applica il font
                        .setPreferTerminalEmulator(true)
                        .setAutoOpenTerminalEmulatorWindow(true);

                terminal = factory.createTerminal();

            } catch (IOException e) {
                throw new RuntimeException("Errore nella creazione del terminale Lanterna", e);
            }
        }
        if (screen == null) {
            try {
                screen = new TerminalScreen(terminal);
                screen.startScreen();
                screen.setCursorPosition(null); // nasconde il cursore
            } catch (IOException e) {
                throw new RuntimeException("Errore durante l'inizializzazione dello Screen", e);
            }
        }
    }

    public static Terminal getTerminal() {
        init();
        return terminal;
    }

    public static Screen getScreen() {
        init();
        return screen;
    }

    public static void clearScreen() {
        init();
        try {
            screen.clear();
            screen.refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static KeyStroke readKey() {
        init();
        try {
            return screen.readInput();
        } catch (IOException e) {
            throw new RuntimeException("Errore lettura input", e);
        }
    }

    public static void shutdown() {
        try {
            if (screen != null) {
                screen.stopScreen();
                screen = null;
            }
            if (terminal != null) {
                terminal.close();
                terminal = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
