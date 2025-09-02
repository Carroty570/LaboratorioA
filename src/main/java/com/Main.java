package com;

import com.controller.UIController;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import com.service.TerminalManager;

/**
 * Classe principale dell’applicazione.
 * <p>
 * Si occupa di:
 * <ul>
 *   <li>Inizializzare il terminale e lo schermo tramite {@link TerminalManager}</li>
 *   <li>Creare e avviare il {@link UIController} che gestisce i menu</li>
 *   <li>Gestire la corretta chiusura delle risorse al termine del programma</li>
 * </ul>
 */

public class Main {

     /**
     * Entry point dell’applicazione.
     *
     * @param args argomenti passati da riga di comando (non utilizzati)
     */
    public static void main(String[] args) {
        Terminal terminal = null;
        Screen screen = null;
        try {
            // Inizializza terminale e schermo
            terminal = TerminalManager.getTerminal();
            screen   = TerminalManager.getScreen();

            // Avvia il controller dell'interfaccia utente
            UIController controller = new UIController(terminal, screen);
            controller.avviaMenu();

        } catch (Exception e) {
            // Stampa eventuali errori su console
            e.printStackTrace();
        } finally {
            // Rilascia le risorse del terminale
            TerminalManager.shutdown();
        }
    }
}
