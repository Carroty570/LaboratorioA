package com.model;

public class Client extends Users {

    private String clientName;
    private String clientEmail;
    private String clientPasswordHash;

    public Client(String name, String email, String passwordHash) {
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

    public String getClientPasswordHash() {
        return clientPasswordHash;
    }

    public void setClientPasswordHash(String clientPasswordHash) {
        this.clientPasswordHash = clientPasswordHash;
    }

    @Override
    public void joinAsGuest() {
        // Non applicabile per client registrato
    }

    @Override
    public void lookMenu() {
        // Implementazione client
    }

    @Override
    public void readFeedback(int feedbackID) {
        // Implementazione client
    }
}
