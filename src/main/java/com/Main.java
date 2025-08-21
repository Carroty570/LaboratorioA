package com;

import com.controller.UIController;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import com.utils.TerminalManager;

public class Main {
    public static void main(String[] args) {
        Terminal terminal = null;
        Screen screen = null;
        try {
            terminal = TerminalManager.getTerminal();
            screen   = TerminalManager.getScreen();

            UIController controller = new UIController(terminal, screen);
            controller.avviaMenu();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            TerminalManager.shutdown();
        }
    }
}
