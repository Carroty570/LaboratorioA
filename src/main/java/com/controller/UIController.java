package com.controller;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import com.model.MenuTipi;
import com.model.Role;
import com.utils.TerminalManager;
import com.view.UIMenu;

import java.util.List;

public class UIController {

    private final Terminal terminal;
    private final Screen screen;
    private final AuthController auth;
    private final UIMenu menu;

    // Opzioni
    private final List<String> opzioniIniziali = List.of(

            "Accesso come ospite",
            "Login",
            "Registrati",
            "Esci"
    );

    private final List<String> opzioniLogin = List.of(

            "Login utente",
            "Login ristoratore",
            "Torna al menu principale"
    );

    private final List<String> opzioniReg = List.of(

            "Registrazione utente",
            "Registrazione ristoratore",
            "Torna al menu principale"
    );

    private final List<String> opzioniPrincipali = List.of(

        "Ricerca",
        "Suggerimenti per zona",
        "Logout",
        "Esci"
    );

    public UIController(Terminal terminal, Screen screen) {

        this.terminal = terminal;
        this.screen = screen;
        this.menu = new UIMenu(terminal, screen);
        this.auth = new AuthController(terminal, screen); // usa metodi role-specific
    }

    public void avviaMenu() throws Exception {

        MenuTipi stato = MenuTipi.INIZIALE;

        while (stato != MenuTipi.ESCI) {
            switch (stato) {
                case INIZIALE     -> stato = menuInterattivo(opzioniIniziali, "WELCOME TO THE KNIFE");
                case LOGIN          -> stato = menuInterattivo(opzioniLogin, "LOGIN");
                case REGISTRAZIONE  -> stato = menuInterattivo(opzioniReg, "REGISTRAZIONE");
                case PRINCIPALE -> stato = menuInterattivo(opzioniPrincipali, "THE KNIFE");
                default             -> stato = MenuTipi.INIZIALE;
            }
        }
        // La chiusura dello Screen/Terminal è gestita dal Main (TerminalManager.shutdown())
    }

    // Menu interattivo con frecce e W/S
    private MenuTipi menuInterattivo(List<String> opzioni, String titolo) throws Exception {

        int selezione = 0;
        menu.printBanner();
        menu.drawMenu(opzioni, selezione, titolo);

        while (true) {
            KeyStroke key = screen.readInput();
            if (key == null) continue;

            KeyType type = key.getKeyType();
            Character ch = key.getCharacter();

            switch (type) {
                case ArrowUp   -> { selezione = (selezione - 1 + opzioni.size()) % opzioni.size(); menu.drawMenu(opzioni, selezione, titolo); }
                case ArrowDown -> { selezione = (selezione + 1) % opzioni.size(); menu.drawMenu(opzioni, selezione, titolo); }
                case Enter     -> { return gestisciScelta(selezione, opzioni); }
                default -> {
                    if (ch != null) {
                        if (ch == 'w' || ch == 'W') { selezione = (selezione - 1 + opzioni.size()) % opzioni.size(); menu.drawMenu(opzioni, selezione, titolo); }
                        else if (ch == 's' || ch == 'S') { selezione = (selezione + 1) % opzioni.size(); menu.drawMenu(opzioni, selezione, titolo); }
                    }
                }
            }
        }
    }

    // Dispatch scelta → nuovo stato
    private MenuTipi gestisciScelta(int sel, List<String> opzioni) {

        String scelta = opzioni.get(sel);
        try {
            switch (scelta) {

                //Menu Iniziale
                case "Accesso come ospite" -> { auth.joinAsGuest(); return MenuTipi.PRINCIPALE; }
                case "Login"               -> { TerminalManager.clearScreen(); return MenuTipi.LOGIN; }
                case "Registrati"          -> { TerminalManager.clearScreen(); return MenuTipi.REGISTRAZIONE; }
                
                //Menu Login
                case "Login utente"        -> {  auth.login(Role.CLIENT, opzioniLogin, sel); return MenuTipi.PRINCIPALE; }
                case "Login ristoratore"   -> { auth.login(Role.ADMIN,  opzioniLogin, sel); return MenuTipi.PRINCIPALE; }

                //Menu Registrazione
                case "Registrazione utente"      -> { auth.registration(Role.CLIENT, opzioniReg, sel); return MenuTipi.PRINCIPALE; }
                case "Registrazione ristoratore" -> { auth.registration(Role.ADMIN,  opzioniReg, sel); return MenuTipi.PRINCIPALE; }

                //Comune nei Menu
                case "Esci"                -> { return MenuTipi.ESCI; }
                case "Torna al menu principale"  -> { TerminalManager.clearScreen(); return MenuTipi.PRINCIPALE; }

                //Menu Principale
                case "Ricerca"               -> {}
                case "Logout"                -> { TerminalManager.clearScreen(); return MenuTipi.INIZIALE; }
                case "Suggerimenti per zona" -> {}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return MenuTipi.PRINCIPALE;
    }
}
