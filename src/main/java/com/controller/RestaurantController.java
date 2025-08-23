package com.controller;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import com.view.UIMenu;

public class RestaurantController{

    
    private final Terminal terminal;
    private final Screen screen;
    private final UIMenu uiMenu;

    public RestaurantController(Terminal terminal, Screen screen){

        this.terminal = terminal;
        this.screen = screen;
        this.uiMenu = new UIMenu(terminal, screen);    

    }
    
    
}