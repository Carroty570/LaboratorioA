package com.utils;

import org.mindrot.jbcrypt.BCrypt;

public class HashUtil {

    // Restituisce un hash sicuro della password fornita
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    // Confronta una password con il suo hash
    public static boolean checkPassword(String password, String hashedPassword) {
        if (password == null || hashedPassword == null) return false;
        return BCrypt.checkpw(password, hashedPassword);
    }
}