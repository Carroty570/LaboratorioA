package com.service;

import com.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Gestione autenticazione su file flat.
 *
 * Formati riga:
 *  - data/clients.txt : name,email,passwordHash,role
 *  - data/admins.txt  : name,email,passwordHash,id    (id = ADM-000001, ADM-000002, ...)
 */
public class AuthService {

    private static final Path DATA_DIR    = Paths.get("data");
    private static final Path CLIENT_FILE = DATA_DIR.resolve("clients.txt");
    private static final Path ADM_FILE    = DATA_DIR.resolve("admins.txt");

    private static final Pattern ADMIN_ID_RX = Pattern.compile("^ADM-(\\d{6})$");

    /* ----------------------------- API GENERICHE ----------------------------- */

    public static boolean userExists(String email) {

        return userExists(Role.CLIENT, email) || userExists(Role.ADMIN, email);
    }

    public static Users loadUserByEmail(String email) {

        Users u = loadUserByEmail(Role.CLIENT, email);
        if (u != null) return u;
        return loadUserByEmail(Role.ADMIN, email);
    }

    public static void saveUser(Users user) {

        saveUser(user.getRole(), user);
    }

    /* ----------------------------- API ROLE-SPECIFIC ----------------------------- */

    public static boolean userExists(Role role, String email) {

        return loadUserByEmail(role, email) != null;
    }

    public static Users loadUserByEmail(Role role, String email) {

        String targetEmail = safe(email);

        if (role == Role.ADMIN) {
            try (BufferedReader reader = Files.newBufferedReader(ADM_FILE, StandardCharsets.UTF_8)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = splitLineAdmin(line);
                    if (parts == null) continue;

                    String name = parts[0];
                    String eml  = parts[1];
                    String pwdH = parts[2];
                    String id   = parts[3];

                    if (eml.equalsIgnoreCase(targetEmail)) {
                        return new Adm(name, eml, pwdH, id);
                    }
                }
            } catch (IOException ignored) {}
            return null;
        } else {
            try (BufferedReader reader = Files.newBufferedReader(CLIENT_FILE, StandardCharsets.UTF_8)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = splitLineClient(line);
                    if (parts == null) continue;

                    String name = parts[0];
                    String eml  = parts[1];
                    String pwdH = parts[2];
                    String rol  = parts[3];

                    if (eml.equalsIgnoreCase(targetEmail) && rol.equalsIgnoreCase(Role.CLIENT.name())) {
                        return new Client(name, eml, pwdH);
                    }
                }
            } catch (IOException ignored) {}
            return null;
        }
    }

    public static void saveUser(Role role, Users user) {

        try {
            Files.createDirectories(DATA_DIR); // <-- assicura che la cartella esista
        } catch (IOException e) {
            throw new RuntimeException("Impossibile creare cartella data/", e);
        }

        if (role == Role.ADMIN) {
            String id;
            if (user instanceof Adm a && a.getStringId() != null && !a.getStringId().isBlank()) {
                id = a.getStringId();
            } else {
                id = nextAdminId();
                user = new Adm(user.getName(), user.getEmail(), user.getPasswordHash(), id);
            }

            try (BufferedWriter writer = Files.newBufferedWriter(ADM_FILE, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                writer.write(safe(user.getName()) + "," +
                             safe(user.getEmail()) + "," +
                             safe(user.getPasswordHash()) + "," +
                             id);
                writer.newLine();
            } catch (IOException e) {
                throw new RuntimeException("Errore scrittura file utenti (" + ADM_FILE + ")", e);
            }
        } else {
            try (BufferedWriter writer = Files.newBufferedWriter(CLIENT_FILE, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                writer.write(safe(user.getName()) + "," +
                             safe(user.getEmail()) + "," +
                             safe(user.getPasswordHash()) + "," +
                             Role.CLIENT.name());
                writer.newLine();
            } catch (IOException e) {
                throw new RuntimeException("Errore scrittura file utenti (" + CLIENT_FILE + ")", e);
            }
        }
    }

    /* ----------------------------- ID ADMIN ----------------------------- */

    public static String nextAdminId() {

        int max = 0;
        try (BufferedReader reader = Files.newBufferedReader(ADM_FILE, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = splitLineAdmin(line);
                if (parts == null) continue;

                String id = parts[3];
                Matcher m = ADMIN_ID_RX.matcher(id);
                if (m.matches()) {
                    int num = Integer.parseInt(m.group(1));
                    if (num > max) max = num;
                }
            }
        } catch (IOException ignored) {}
        return String.format("ADM-%06d", max + 1);
    }

    /* ----------------------------- Utility ----------------------------- */

    public static String hashPassword(String password) {

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(safe(password).getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Errore hashing password", e);
        }
    }

    private static String[] splitLineClient(String line) {

        if (line == null) return null;
        String[] parts = line.split(",", -1);
        if (parts.length < 4) return null;
        for (int i = 0; i < 4; i++) parts[i] = parts[i].trim();
        return new String[]{ parts[0], parts[1], parts[2], parts[3] };
    }

    private static String[] splitLineAdmin(String line) {

        if (line == null) return null;
        String[] parts = line.split(",", -1);
        if (parts.length < 4) return null;
        for (int i = 0; i < 4; i++) parts[i] = parts[i].trim();
        if (parts[0].equalsIgnoreCase("name") && parts[1].equalsIgnoreCase("email")) return null;
        return new String[]{ parts[0], parts[1], parts[2], parts[3] };
    }

    private static String safe(String s) {
        
        return (s == null) ? "" : s.trim();
    }
}
