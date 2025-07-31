package com.model;

import java.util.Map;

public class Adm extends Users{

    private String admName;
    private String admEmail;
    private String admPasswordHash;

    public Adm(String name, String email, String hashed) {
        this.admName = name;
        this.admEmail = email;
        this.admPasswordHash = hashed;
    }

    public String getAdmName() {
        return admName;
    }

    public void setAdmName(String admName) {
        this.admName = admName;
    }

    public String getAdmEmail() {
        return admEmail;
    }

    public void setAdmEmail(String admEmail) {
        this.admEmail = admEmail;
    }

    public String getAdmPasswordHash() {
        return admPasswordHash;
    }

    public void setAdmPasswordHash(String admPasswordHash) {
        this.admPasswordHash = admPasswordHash;
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