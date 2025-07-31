package com.utils;

import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;

public class HashUtil {
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean checkPassword(String password) {
        return BCrypt.checkpw(password);
    }
}
