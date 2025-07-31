package com.controller;

import com.model.*;
import com.utils.HashUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UIController {

    private List<Users> users;
    private Users currentUser;

    public UIController() {
        this.users = new ArrayList<>();
        this.currentUser = null;
    }

    public void joinAsGuest() {
        Guest guest = new Guest();
        this.currentUser = guest;
        System.out.println("Accesso come guest effettuato.");
    }

    public void login(String email, String password) {
        for (Users user : users) {
            if (user instanceof Client client) {
                if (client.getClientEmail().equals(email) &&
                    HashUtil.checkPassword(password, client.getClientPasswordHash())) {
                    currentUser = client;
                    System.out.println("Login cliente effettuato.");
                    return;
                }
            } else if (user instanceof Adm admin) {
                if (admin.getAdmEmail().equals(email) &&
                    HashUtil.checkPassword(password, admin.getAdmPasswordHash())) {
                    currentUser = admin;
                    System.out.println("Login admin effettuato.");
                    return;
                }
            }
        }
        System.out.println("Credenziali non valide.");
    }

    public void registerClient(String name, String email, String password) {
        String hashed = HashUtil.hashPassword(password);
        Client client = new Client(name, email, hashed);
        users.add(client);
        System.out.println("Registrazione cliente avvenuta con successo.");
    }

    public void registerAdmin(String name, String email, String password) {
        String hashed = HashUtil.hashPassword(password);
        Adm admin = new Adm(name, email, hashed);
        users.add(admin);
        System.out.println("Registrazione admin avvenuta con successo.");
    }

    public Users getCurrentUser() {
        return currentUser;
    }
}