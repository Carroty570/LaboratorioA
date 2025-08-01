package com;
import java.io.IOException;

import com.controller.UIController;
import com.view.UIMenu;

public class Main {
    public static void main(String[] args) throws IOException {
        
        System.setProperty("jline.terminal", "windows");
        System.out.println("Benvenuto nel sistema di gestione ristoranti!");
        UIMenu menu = new UIMenu();
        menu.start();

    }
}
