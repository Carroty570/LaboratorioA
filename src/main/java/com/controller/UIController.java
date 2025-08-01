package com.controller;

import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;

public class UIController {
    private Map<String, String> utenti;

    public UIController() {
        utenti = new HashMap<>();
        // Utente admin di default
        utenti.put("admin", hashPassword("admin"));
    }

    public boolean registra(String username, String password) {
        if (utenti.containsKey(username)) {
            return false;
        }
        utenti.put(username, hashPassword(password));
        return true;
    }

    public boolean login(String username, String password) {
        if (!utenti.containsKey(username)) {
            return false;
        }
        String hashed = utenti.get(username);
        return checkPassword(password, hashed);
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private boolean checkPassword(String password, String hashed) {
        return BCrypt.checkpw(password, hashed);
    }
}
