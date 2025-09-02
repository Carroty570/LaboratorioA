package com.model;


public class Guest extends Users {

    public Guest(String name) {
        
        super(name, null, null, Role.GUEST);
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