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

    // addFavorite, addFeedback, etc...
}
