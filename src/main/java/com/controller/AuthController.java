package com.controller;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

public class AuthController{

    private static final String File = "users.txt";
   
    // Metodi avviati solo da main e non dal menu (non dal menu)
    public boolean login(int selezione) {
        if (selezione == 0){

            Console console = System.console();
            String emailUser = console.readLine("Email: ");

            if (emailUser != null && !EMAIL_RX.matcher(emailUser).matches()) {

                throw new IllegalArgumentException("Formato di email non valido");
            }

            char[] passwordChars = console.readPassword("Password: ");
            String passwordUser = new String(passwordChars);
            String hashedPassword = hashPassword(passwordUser);

            if (verificaCredenziali(emailUser, hashedPassword, File)) {
                System.out.println("Login effettuato con successo. Benvenuto, " + emailUser + "!");
                return true;
            } else {
                System.out.println("Credenziali non valide o utente non registrato.");
                return false;
            }
        }else if(selezione == 1){

            Console console = System.console();
            if (console == null) {
                throw new IllegalStateException("Console non disponibile.");
            }

            String emailAdm = console.readLine("Inserire la propria mail:\n");
            char[] passwordChars = console.readPassword("Password: ");
            String passwordAdm = new String(passwordChars);
            String hashedPassword = hashPassword(passwordAdm);

            if (verificaCredenziali(emailAdm, hashedPassword, File)) {
                System.out.println("Login effettuato con successo. Benvenuto, " + emailAdm + "!");
                return true;
            } else {
                System.out.println("Credenziali non valide o ristoratore non registrato.");
                return false;
            }
        }
        throw new IllegalStateException("Login non effettuato");
    }

    public boolean registration(int selezione) {
        
        boolean loop = true;

        if (selezione == 0){

            Console console = System.console();
            if (console == null) {
                
                throw new IllegalStateException("Console non disponibile.");
            }
            String emailUser = console.readLine("Inserire la mail che si desidera registrare:\n");

            if(utenteEsiste(emailUser, File)){
                throw new IllegalStateException("Console non disponibile.");
            }

            while (loop){
                char[] passwordChars = console.readPassword("Scegliere una password: ");
                String passwordUser = new String(passwordChars);
                char[] passwordCharConferma = console.readPassword("Riscrivere la password per conferma: ");
                String passwordUserConferma = new String(passwordCharConferma);

                if(passwordUser.equals(passwordUserConferma)){

                    String hashedPassword = hashPassword(passwordUser);
                    salvaUtente(emailUser, hashedPassword, File);
                    System.out.println("Registrazione completata con successo. Ora puoi effettuare il login.");
                    return true;
                    
                }else{
                    System.out.println("Le password non combaciano, riprovare. ");    
                }
            }
             
        }else if(selezione == 1){

        Console console = System.console();

            if (console == null) {
                throw new IllegalStateException("Console non disponibile.");
            }

            String emailAdm = console.readLine("Inserire la mail che si desidera registrare:\n");

            if(utenteEsiste(emailAdm, File)){
                throw new IllegalStateException("Console non disponibile.");
            }

            while (loop){
                char[] passwordChars = console.readPassword("Scegliere una password: ");
                String passwordAdm = new String(passwordChars);
                char[] passwordCharConferma = console.readPassword("Riscrivere la password per conferma: ");
                String passwordAdmConferma = new String(passwordCharConferma);

                if(passwordAdm.equals(passwordAdmConferma)){

                    String hashedPassword = hashPassword(passwordAdm);
                    salvaUtente(emailAdm, hashedPassword, File);
                    System.out.println("Registrazione completata con successo. Ora puoi effettuare il login.");
                    return true;
                }else{
                    System.out.println("Le password non combaciano, riprovare. ");    
                }
            }  
        }
        throw new IllegalStateException("Registrazione non effettuata");
    }

    public void joinAsGuest() {
        System.out.println("Accesso come ospite effettuato.");
    }

    private static boolean verificaCredenziali(String username, String hashedPassword, String File_Name) {
        try (BufferedReader reader = new BufferedReader(new FileReader(File_Name))) {
            String riga;
            while ((riga = reader.readLine()) != null) {
                String[] parts = riga.split(",");
                if (parts.length == 2 && parts[0].equals(username) && parts[1].equals(hashedPassword)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Errore durante la lettura del file: " + e.getMessage());
        }
        return false;
    }

    private static boolean utenteEsiste(String username, String File_Name) {
        try (BufferedReader reader = new BufferedReader(new FileReader(File_Name))) {
            String riga;
            while ((riga = reader.readLine()) != null) {
                String[] parts = riga.split(",");
                if (parts.length >= 1 && parts[0].equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            // Ignora se il file non esiste
        }
        return false;
    }

    private static void salvaUtente(String username, String hashedPassword, String File_Name) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(File_Name, true))) {
            writer.write(username + "," + hashedPassword);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Errore durante la scrittura del file: " + e.getMessage());
        }
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b)); // formato esadecimale
            }
            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            System.out.println("Errore durante la cifratura: " + e.getMessage());
            return null;
        }
    }
}