package com.controller;

import com.utils.CmdUtil;
import java.io.Console;
import java.io.IOException;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;


public class UIController {

    private Terminal terminal;
    

    
    //Controller che gestisce anche il main per non appesantirlo
    public void avvia(String[] args) {
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "login" -> eseguiLogin();
                case "register" -> eseguiRegistrazione();
                case "guest" -> eseguiGuest();

                default -> new com.view.UIMenu().printMenu(); // fallback al menu
            }
        } else {
            new com.view.UIMenu().printMenu(); // Nessun parametro: mostra menu
        }
    }


    //Metodi che aprono solo la nuova finestra del cmd e mandano il parametro scelto al main
    public void login() {
        try {
            CmdUtil.apriNuovoTerminale("login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registrazione() {
        try {
            CmdUtil.apriNuovoTerminale("register");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void accessoGuest() {
        try {
            CmdUtil.apriNuovoTerminale("guest");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Metodi avviati solo da main e non dal menu (non dal menu)
    public void eseguiLogin() {
        Console console = System.console();
        if (console == null) {
            System.err.println("Console non disponibile.");
            return;
        }
        String email = console.readLine("Email: ");
        char[] passwordChars = console.readPassword("Password: ");
        String password = new String(passwordChars);
        System.out.println("Login effettuato per: " + email);
    }

    public void eseguiRegistrazione() {
        Console console = System.console();
        if (console == null) {
            System.err.println("Console non disponibile.");
            return;
        }
        String email = console.readLine("Email: ");
        char[] passwordChars = console.readPassword("Password: ");
        String password = new String(passwordChars);
        System.out.println("Registrazione effettuata per: " + email);
    }

    public void eseguiGuest() {
        System.out.println("Accesso come ospite effettuato.");
    }

    
}
