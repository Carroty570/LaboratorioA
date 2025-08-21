package com.utils;

import com.model.*;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Gestione autenticazione su file flat.
 * Formato riga: name,email,passwordHash,role
 *  - clients.txt  : solo utenti con Role.CLIENT
 *  - admins.txt   : solo utenti con Role.ADMIN
 */
public class AuthUtils {

    private static final String CLIENT_FILE = "clients.txt";
    private static final String ADM_FILE    = "admins.txt";

    /* ----------------------------- API GENERICHE (compat) ----------------------------- */

    /** Verifica esistenza utente cercando in entrambi i file (client + admin). */
    public static boolean userExists(String email) {
        return userExists(Role.CLIENT, email) || userExists(Role.ADMIN, email);
    }

    /** Carica utente cercando in entrambi i file. Ritorna Client o Adm a seconda di dove viene trovato, altrimenti null. */
    public static Users loadUserByEmail(String email) {
        Users u = loadUserByEmail(Role.CLIENT, email);
        if (u != null) return u;
        return loadUserByEmail(Role.ADMIN, email);
    }

    /** Salva utente nel file appropriato in base al suo ruolo. */
    public static void saveUser(Users user) {
        saveUser(user.getRole(), user);
    }

    /* ----------------------------- API ROLE-SPECIFIC ----------------------------- */

    /** Verifica esistenza utente nel file specifico per ruolo. */
    public static boolean userExists(Role role, String email) {
        return loadUserByEmail(role, email) != null;
    }

    /**
     * Carica utente dal file specifico per ruolo.
     * Ritorna un Client se role == CLIENT, oppure un Adm se role == ADMIN. Ritorna null se non trovato.
     */
    public static Users loadUserByEmail(Role role, String email) {
        String file = (role == Role.ADMIN) ? ADM_FILE : CLIENT_FILE;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String targetEmail = safe(email);
            while ((line = reader.readLine()) != null) {
                String[] parts = splitLine(line);
                if (parts == null) continue;

                String name = parts[0];
                String eml  = parts[1];
                String pwdH = parts[2];
                String rol  = parts[3];

                if (eml.equalsIgnoreCase(targetEmail) && rol.equalsIgnoreCase(role.name())) {
                    return (role == Role.CLIENT)
                            ? new Client(name, eml, pwdH)
                            : new Adm(name, eml, pwdH);
                }
            }
        } catch (IOException ignored) {
            // se il file non esiste ancora, per noi "non trovato"
        }
        return null;
    }

    /** Salva utente nel file legato al ruolo. Crea il file se non esiste. */
    public static void saveUser(Role role, Users user) {
        String file = (role == Role.ADMIN) ? ADM_FILE : CLIENT_FILE;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(safe(user.getName()) + "," +
                         safe(user.getEmail()) + "," +
                         safe(user.getPasswordHash()) + "," +
                         role.name());
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Errore scrittura file utenti (" + file + ")", e);
        }
    }

    /* ----------------------------- Utility ----------------------------- */

    /** Hash SHA-256 semplice (hex). */
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(safe(password).getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Errore hashing password", e);
        }
    }

    /** Pulisce e normalizza la riga letta: ritorna array 4 campi oppure null se invalida. */
    private static String[] splitLine(String line) {
        if (line == null) return null;
        String[] parts = line.split(",");
        if (parts.length < 4) return null;
        // trim dei 4 campi attesi
        for (int i = 0; i < 4; i++) parts[i] = parts[i].trim();
        return new String[]{ parts[0], parts[1], parts[2], parts[3] };
    }

    /** Evita NPE e rimuove spazi superflui. */
    private static String safe(String s) {
        return (s == null) ? "" : s.trim();
    }
}
