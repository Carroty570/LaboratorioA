package com;

import java.io.IOException;
import com.view.UIMenu;
import com.controller.UIController;

public class Main {
    public static void main(String[] args) throws IOException {
        UIController controller = new UIController();

        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "login" -> controller.eseguiLogin();
                case "register" -> controller.eseguiRegistrazione();
                case "guest" -> controller.eseguiGuest();
                default -> new UIMenu().start();
            }
        } else {
            new UIMenu().start();
        }
    }
}
