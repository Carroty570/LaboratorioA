package com.service;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;

public class TerminalManager {

    private static Terminal terminal;
    private static Screen screen;

    private static void init() {
        if (terminal == null) {

            try {
                // 1) Font monospaziato
                Font baseFont;
                try {
                    baseFont = new Font("Consolas", Font.PLAIN, 18);
                    if (!baseFont.getFamily().equalsIgnoreCase("Consolas")) {
                        throw new RuntimeException("Font non disponibile, uso Monospaced");
                    }
                } catch (Exception ex) {
                    baseFont = new Font(Font.MONOSPACED, Font.PLAIN, 18);
                }

                // 2) Configurazione font per Swing
                SwingTerminalFontConfiguration fontCfg = SwingTerminalFontConfiguration.newInstance(baseFont);

                // 3) Crea terminale, ma NON aprire ancora la finestra
                DefaultTerminalFactory factory = new DefaultTerminalFactory()
                        .setInitialTerminalSize(new TerminalSize(140, 40)) // scegli tu la size in CELLE
                        .setTerminalEmulatorTitle("The Knife")
                        .setTerminalEmulatorFontConfiguration(fontCfg)
                        .setPreferTerminalEmulator(true)
                        .setAutoOpenTerminalEmulatorWindow(false);

                terminal = factory.createTerminal();

                if (terminal instanceof SwingTerminalFrame frame) {

                    // finestra decorata e NON ridimensionabile
                    frame.setUndecorated(false);
                    frame.setResizable(false);
                    frame.setLocationByPlatform(false); // gestiamo noi il posizionamento

                    // mostra la finestra per ottenere la size effettiva (dipende dal font)
                    frame.setVisible(true);

                    // Calcola spazio utile (rispetta taskbar/dock) e centra
                    GraphicsConfiguration gc = frame.getGraphicsConfiguration();
                    if (gc == null) {
                        gc = GraphicsEnvironment.getLocalGraphicsEnvironment()
                                .getDefaultScreenDevice().getDefaultConfiguration();
                    }
                    Rectangle screenBounds = gc.getBounds();
                    Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
                    Rectangle usable = new Rectangle(
                            screenBounds.x + insets.left,
                            screenBounds.y + insets.top,
                            screenBounds.width  - insets.left - insets.right,
                            screenBounds.height - insets.top  - insets.bottom
                    );

                    // centra la finestra nell’area utile
                    Dimension win = frame.getSize();
                    int cx = usable.x + (usable.width  - win.width)  / 2;
                    int cy = usable.y + (usable.height - win.height) / 2;
                    final Point lockLocation = new Point(cx, cy);
                    frame.setLocation(lockLocation);

                    // Impedisci lo spostamento: se si muove, riportala subito al punto bloccato
                    frame.addComponentListener(new ComponentAdapter() {
                        @Override public void componentMoved(ComponentEvent e) {
                            Point p = frame.getLocation();
                            if (!p.equals(lockLocation)) {
                                // ricentra solo se davvero cambiata (evita loop)
                                frame.setLocation(lockLocation);
                            }
                        }
                    });
                }

            } catch (IOException e) {
                throw new RuntimeException("Errore nella creazione del terminale Lanterna", e);
            }
        }
        if (screen == null) {
            try {

                screen = new TerminalScreen(terminal);
                screen.startScreen();
                screen.doResizeIfNecessary();   // allinea buffer alla size corrente
                screen.setCursorPosition(null); // nascondi cursore
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
