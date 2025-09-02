package com.model;

import java.util.Objects;

public class Client extends Users {

    public Client(String name, String email, String passwordHash) {

        super(name, email, passwordHash, Role.CLIENT);
    }


    @Override
    public void joinAsGuest() {
        
    }

    @Override
    public void lookMenu() {
        
    }

    @Override
    public void readFeedback(int feedbackID) {
        
    }
}