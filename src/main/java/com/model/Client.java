package com.model;

import java.util.Map;

public class Client extends Users {
    private String clientName;
    private String clientEmail;
    private Map<String, String> clientPasswordHash;

    @Override
    public void joinAsGuest() {}

    @Override
    public void lookMenu() {}

    @Override
    public void readFeedback(int feedbackID) {}

// Getters for Client attributes
    public String getClientEmail() {

        return clientEmail;
    }

    public String getClientName() {

        return clientName;
    }

    public Map<String, String> getClientPasswordHash() {

        return clientPasswordHash;
    }


    // addFavorite, addFeedback, etc...
}
