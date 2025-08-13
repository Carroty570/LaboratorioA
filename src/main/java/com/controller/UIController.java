package com.controller;

import com.utils.CmdUtil;

public class UIController {

    AuthController auth = new AuthController();

    //Controller che gestisce anche il main per non appesantirlo
    public void avvia(String[] args) {
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "loginuser" -> auth.eseguiLogin(0);
                case "loginadm" -> auth.eseguiLogin(1);
                case "registeruser" -> auth.eseguiRegistrazione(0);
                case "registeradm" -> auth.eseguiRegistrazione(1);    
                case "guest" -> auth.eseguiGuest();

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

}
