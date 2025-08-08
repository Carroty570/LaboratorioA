package com.controller;

import com.utils.CmdUtil;
import java.io.Console;

public class UIController {

    //Controller che gestisce anche il main per non appesantirlo
    public void avvia(String[] args) {
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "loginuser" -> eseguiLogin(0);
                case "loginadm" -> eseguiLogin(1);
                case "registeruser" -> eseguiRegistrazione(0);
                case "registeradm" -> eseguiRegistrazione(1);    
                case "guest" -> eseguiGuest();

                default -> new com.view.UIMenu().avviaMenu(); // fallback al menu
            }
        } else {
            new com.view.UIMenu().avviaMenu(); // Nessun parametro: mostra menu
        }
    }

    //Metodi che aprono solo la nuova finestra del cmd e mandano il parametro scelto al main
    public void login(int scelta) {
        if (scelta == 0){
            try {
                CmdUtil.apriNuovoTerminale("loginUser");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(scelta ==1){
            try {
                CmdUtil.apriNuovoTerminale("loginAdm");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void registrazione(int scelta) {
        if (scelta == 0){
            try {
                CmdUtil.apriNuovoTerminale("registerUser");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (scelta ==1){
            try {
                CmdUtil.apriNuovoTerminale("registerAdm");
            } catch (Exception e) {
                e.printStackTrace();
            }
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
    public void eseguiLogin(int selezione) {
        if (selezione == 0){}
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

    public void eseguiRegistrazione(int selezione) {
        if(selezione == 0){}
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
