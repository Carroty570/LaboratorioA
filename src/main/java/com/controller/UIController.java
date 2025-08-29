package com.controller;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import com.view.UIMenu;
import com.model.*;
import com.service.TerminalManager;

import java.util.List;

public class UIController {

    private final Terminal terminal;
    private final Screen screen;
    private final AuthController auth;
    private final UIMenu menu;
    private final RestaurantController restaurant;
    private final SearchController search;


    private MenuTipi stato = null;
    private currentRole role;

    public boolean logineffettuato = false;

    public UIController(Terminal terminal, Screen screen) {

        this.terminal = terminal;
        this.screen = screen;

        this.search = new SearchController(screen, terminal);
        this.menu = new UIMenu(terminal, screen);
        this.auth = new AuthController(terminal, screen);
        this.restaurant = new RestaurantController(terminal, screen);

        this.role = new currentRole(null);
    }
    
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

    private final List<String> opzioniGuest = List.of(

        "Ricerca",
        "Login",
        "Registrati",
        "Esci"
    );

    private final List<String> opzioniAdmin = List.of(

    "Ricerca",
    "Suggerimenti per zona",
    "Visualizza i tuoi ristoranti",
    "Aggiungi un ristorante",
    "Rimuovi un ristorante",
    "Logout",
    "Esci"
    );

    private final List<String> viewRistoranti = List.of(

    "Visualizza i dettagli",
    "Visualizza le recensioni",
    "Torna al menu ristoratori"
    );



    public void avviaMenu() throws Exception {

        stato = MenuTipi.INIZIALE;

        while (stato != MenuTipi.ESCI) {
            switch (stato) {

                case INIZIALE      -> { if(role.getCurrentRole() != null && role.getCurrentRole().equals(Role.GUEST)){
                                        stato = menuInterattivo(opzioniGuest, "THE KNIFE (guest)");
                                      } else {
                                        stato = menuInterattivo(opzioniIniziali, "WELCOME TO THE KNIFE");
                                      }
                                    }

                case LOGIN         -> stato = menuInterattivo(opzioniLogin, "LOGIN");
                case REGISTRAZIONE -> stato = menuInterattivo(opzioniReg, "REGISTRAZIONE");

                case PRINCIPALE    -> { if(role.getCurrentRole().equals(Role.GUEST)){
                                        stato = menuInterattivo(opzioniGuest, "THE KNIFE (guest)");
                                      } else {
                                        stato = menuInterattivo(opzioniPrincipali, "THE KNIFE");
                                      }
                                    }

                case GUEST         -> { role.setCurrentRole(Role.GUEST); 
                                        stato = menuInterattivo(opzioniGuest, "THE KNIFE (guest)");}
                
                case ADMIN        -> { role.setCurrentRole(Role.ADMIN);
                                        stato = menuInterattivo(opzioniAdmin, "THE KNIFE (admin)");}

                case RISTORANTI -> { if(role.getCurrentRole().equals(Role.ADMIN)){
                                     stato = menuInterattivo(viewRistoranti, "THE KNIFE (ristoranti admin)");
                                  }  else {
                                     stato = menuInterattivo(opzioniPrincipali, "THE KNIFE (ristoranti guest)");
                                  }
                                }
                
                default            -> stato = MenuTipi.INIZIALE;
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
            switch (scelta.toLowerCase()) {

                //Menu Iniziale
                case "accesso come ospite" -> { auth.joinAsGuest(); return MenuTipi.GUEST; }
                case "login"               -> { TerminalManager.clearScreen(); return MenuTipi.LOGIN; }
                case "registrati"          -> { TerminalManager.clearScreen(); return MenuTipi.REGISTRAZIONE; }
                
                //Menu Login
                case "login utente"        -> { if(auth.loginSuccess(Role.CLIENT, opzioniLogin, sel)){ return MenuTipi.PRINCIPALE;} 
                                                else{ return MenuTipi.INIZIALE;} }

                case "login ristoratore"   -> { if(auth.loginSuccess(Role.ADMIN, opzioniLogin, sel)){ return MenuTipi.ADMIN;} 
                                                else{ return MenuTipi.INIZIALE;} }

                //Menu Registrazione
                case "registrazione utente"      -> { auth.registration(Role.CLIENT, opzioniReg, sel); return MenuTipi.INIZIALE; }
                case "registrazione ristoratore" -> { auth.registration(Role.ADMIN,  opzioniReg, sel); return MenuTipi.INIZIALE; }
                
                //Comune nei Menu
                case "esci"                -> { return MenuTipi.ESCI; }
                case "torna al menu principale"  -> { TerminalManager.clearScreen(); return MenuTipi.INIZIALE; }

                //Menu Principale
                case "ricerca"               -> {}
                case "logout"                -> { TerminalManager.clearScreen(); role.setCurrentRole(null); return MenuTipi.INIZIALE; }
                case "suggerimenti per zona" -> {}

                //Menu Admin
                case "aggiungi un ristorante" ->{restaurant.addRestaurant(Role.ADMIN, opzioni, sel); return MenuTipi.ADMIN;}
                case "rimuovi un ristorante" ->{restaurant.delRestaurant(Role.ADMIN, opzioni, sel); return MenuTipi.ADMIN;}
                case "visualizza i tuoi ristoranti" ->{TerminalManager.clearScreen(); return MenuTipi.RISTORANTI; }
                
                //Menu Visualizzazione Ristoranti
                case "visualizza i dettagli" ->{restaurant.viewRestaurantDetails(Role.ADMIN, opzioni, sel);}
                case "visualizza le recensioni" ->{}
                case "torna al menu ristoratori" -> {TerminalManager.clearScreen(); return MenuTipi.ADMIN;}

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stato;
    }
}
