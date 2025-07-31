package com.model;

import java.util.Map;

public class Client extends Users {
    private String clientName;
    private String clientEmail;
    private Map<String, String> clientPasswordHash;

    private String admName;
    private String admEmail;
    private String admPasswordHash;

    public Client(String name, String email, Map<String, String> passwordHash) {
        this.clientName = name;
        this.clientEmail = email;
        this.clientPasswordHash = passwordHash;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public Map<String, String> getClientPasswordHash() {
        return clientPasswordHash;
    }

    public void setClientPasswordHash(Map<String, String> clientPasswordHash) {
        this.clientPasswordHash = clientPasswordHash;
    }

    @Override
    public void joinAsGuest() {
        // Non applicabile per admin
    }

    @Override
    public void lookMenu() {
        // Implementazione admin
    }

    @Override
    public void readFeedback(int feedbackID) {
        // Implementazione admin
    }

    
}
