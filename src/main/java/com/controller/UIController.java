package com.controller;

import com.utils.CmdUtil;

public class UIController {
    

    AuthController auth = new AuthController();
    String parametro;

    //Controller che gestisce anche il main per non appesantirlo
    public void controllerMenu(String parametro) {

        switch (parametro) {
            case "loginUser" -> auth.login(0);
            case "loginAdm" -> auth.login(1);
            case "registerUser" -> auth.registration(0);
            case "registerAdm" -> auth.registration(1);    
            case "guest" -> auth.joinAsGuest();

            default -> new com.view.UIMenu().avviaMenu(); // fallback al menu
        }  
    }

    

    //Metodi che aprono solo la nuova finestra del cmd e mandano il parametro scelto al main
    public void login(int scelta) {

        if (scelta == 0){
            parametro = "loginUser";
            controllerMenu(parametro);

        } else if(scelta ==1){
            parametro = "loginAdm";
            controllerMenu(parametro);
        }
    }

    public void registrazione(int scelta) {
        if (scelta == 0){
            parametro = "registerUser";
            controllerMenu(parametro);

        } else if(scelta ==1){
            parametro = "registerAdm";
            controllerMenu(parametro);
        }
    }

    public void joinAsGuest() {
        parametro = "guest";
        controllerMenu(parametro);
    }
}
