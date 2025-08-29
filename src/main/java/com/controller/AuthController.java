package com.controller;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;

import com.model.*;
import com.service.*;
import com.view.UIMenu;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class AuthController {


    private final UIMenu uiMenu;
    private final InputService input;

    private static final Pattern EMAIL_RX = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    public AuthController(Terminal terminal, Screen screen) {

        this.uiMenu = new UIMenu(terminal, screen);
        this.input = new InputService(terminal, screen);
    }

    // -------------------- LOGIN (role-specific) --------------------
    public Users login(Role role, List<String> opzioni, int selezione) throws IOException {

        TerminalManager.clearScreen();
        uiMenu.drawMenu(opzioni, selezione, "LOGIN " + role.name());

        String email = input.readLine("Email: ");
        while (!EMAIL_RX.matcher(email).matches()) {
            email = input.readLine("Formato email non valido, riprova: ");
        }

        String password = input.readPassword("Password: ");
        String hashedPassword = AuthService.hashPassword(password);

        // carico l'utente dal file giusto
        Users user = AuthService.loadUserByEmail(role, email);

        if (user != null && hashedPassword.equals(user.getPasswordHash())) {
            uiMenu.showMessage("Login effettuato! Benvenuto, " + user.getName());
            return user;
        }

        uiMenu.showMessage("Credenziali non valide per " + role.name() + ".");
        return null;
    }

    // -------------------- REGISTRAZIONE (role-specific) --------------------
    public Users registration(Role role, List<String> opzioni, int selezione) throws IOException {

        TerminalManager.clearScreen();
        uiMenu.drawMenu(opzioni, selezione, "REGISTRAZIONE " + role.name());

        String name = input.readLine("Nome: ");
        while (name.isBlank()) {
            name = input.readLine("Il nome non può essere vuoto. Inserisci nome: ");
        }

        String email = input.readLine("Email: ");
        while (!EMAIL_RX.matcher(email).matches()) {
            email = input.readLine("Formato email non valido, riprova: ");
        }

        // esiste già in QUEL ruolo?
        if (AuthService.userExists(role, email)) {
            uiMenu.showMessage("⚠ Esiste già un " + role.name().toLowerCase() + " registrato con questa email.");
            return null;
        }

        String password = input.readPassword("Scegliere la password: ");
        String passwordCheck = input.readPassword("Ripetere la password per conferma: ");
        while (password.isBlank() || !password.equals(passwordCheck)) {
            password = input.readPassword("La password è vuota o non combaciano. Inserire la password: ");
            passwordCheck = input.readPassword("Reinserire la password: ");
        }

        String hashedPassword = AuthService.hashPassword(password);

        Users newUser = (role == Role.CLIENT)
                ? new Client(name, email, hashedPassword)
                : new Adm(name, email, hashedPassword);

        // salva NEL file del ruolo richiesto
        AuthService.saveUser(role, newUser);
        uiMenu.showMessage("Registrazione " + role.name().toLowerCase() + " completata. Ora puoi fare login.");
        return newUser;
    }

    // -------------------- GUEST --------------------
    public Users joinAsGuest() throws IOException {

        TerminalManager.clearScreen();
        uiMenu.drawMenu(List.of(""), 0, "OSPITE");

        String guestName = input.readLine("Inserisci il tuo nome: ");
        while (guestName.isBlank()) {
            guestName = input.readLine("Il nome non può essere vuoto. Inserisci il tuo nome: ");
        }

        Users guest = new Guest(guestName);
        uiMenu.showMessage("Accesso come ospite effettuato. Benvenuto, " + guestName);
        return guest;
    }

    public boolean loginSuccess(Role role, List<String> opzioni, int selezione) throws IOException{

        if(login(role, opzioni, selezione)!= null){
            return true;
        }
        return false;

    }  
}
