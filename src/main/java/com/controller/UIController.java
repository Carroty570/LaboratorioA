package com.controller;

import com.utils.CmdUtil;
import java.io.Console;

public class UIController {

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
