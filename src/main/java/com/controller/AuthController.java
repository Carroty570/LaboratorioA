package com.controller;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;

import com.model.*;
import com.utils.AuthUtils;
import com.utils.TerminalManager;
import com.view.UIMenu;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class AuthController {

    private final Terminal terminal;
    private final Screen screen;
    private final UIMenu uiMenu;

    private static final Pattern EMAIL_RX = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    public AuthController(Terminal terminal, Screen screen) {
        this.terminal = terminal;
        this.screen = screen;
        this.uiMenu = new UIMenu(terminal, screen);
    }

    // -------------------- LOGIN (role-specific) --------------------
    public Users login(Role role, List<String> opzioni, int selezione) throws IOException {

        TerminalManager.clearScreen();
        uiMenu.drawMenu(opzioni, selezione, "LOGIN " + role.name());

        String email = readLine("Email: ");
        while (!EMAIL_RX.matcher(email).matches()) {
            email = readLine("Formato email non valido, riprova: ");
        }

        String password = readPassword("Password: ");
        String hashedPassword = AuthUtils.hashPassword(password);

        // carico l'utente dal file giusto
        Users user = AuthUtils.loadUserByEmail(role, email);

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

        String name = readLine("Nome: ");
        while (name.isBlank()) {
            name = readLine("Il nome non può essere vuoto. Inserisci nome: ");
        }

        String email = readLine("Email: ");
        while (!EMAIL_RX.matcher(email).matches()) {
            email = readLine("Formato email non valido, riprova: ");
        }

        // esiste già in QUEL ruolo?
        if (AuthUtils.userExists(role, email)) {
            uiMenu.showMessage("⚠ Esiste già un " + role.name().toLowerCase() + " registrato con questa email.");
            return null;
        }

        String password = readPassword("Scegliere la password: ");
        String passwordCheck = readPassword("Ripetere la password per conferma: ");
        while (password.isBlank() || !password.equals(passwordCheck)) {
            password = readPassword("La password è vuota o non combaciano. Inserire la password: ");
            passwordCheck = readPassword("Reinserire la password: ");
        }

        String hashedPassword = AuthUtils.hashPassword(password);

        Users newUser = (role == Role.CLIENT)
                ? new Client(name, email, hashedPassword)
                : new Adm(name, email, hashedPassword);

        // salva NEL file del ruolo richiesto
        AuthUtils.saveUser(role, newUser);
        uiMenu.showMessage("Registrazione " + role.name().toLowerCase() + " completata. Ora puoi fare login.");
        return newUser;
    }

    // -------------------- GUEST --------------------
    public Users joinAsGuest() throws IOException {

        TerminalManager.clearScreen();
        uiMenu.drawMenu(List.of(""), 0, "OSPITE");

        String guestName = readLine("Inserisci il tuo nome: ");
        while (guestName.isBlank()) {
            guestName = readLine("Il nome non può essere vuoto. Inserisci il tuo nome: ");
        }

        Users guest = new Guest(guestName);
        uiMenu.showMessage("Accesso come ospite effettuato. Benvenuto, " + guestName);
        return guest;
    }

    // -------------------- Input helpers (Lanterna/Screen) --------------------
    private String readLine(String prompt) throws IOException {
        uiMenu.redrawInputLine(prompt, "");
        StringBuilder sb = new StringBuilder();
        while (true) {
            KeyStroke key = screen.readInput();
            if (key == null) continue;
            KeyType kt = key.getKeyType();
            if (kt == KeyType.Enter) break;
            if (kt == KeyType.Backspace && sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            } else if (key.getCharacter() != null && !Character.isISOControl(key.getCharacter())) {
                sb.append(key.getCharacter());
            }
            uiMenu.redrawInputLine(prompt, sb.toString());
        }
        return sb.toString().trim();
    }

    private String readPassword(String prompt) throws IOException {
        uiMenu.redrawInputLine(prompt, "");
        StringBuilder sb = new StringBuilder();
        while (true) {
            KeyStroke key = screen.readInput();
            if (key == null) continue;
            KeyType kt = key.getKeyType();
            if (kt == KeyType.Enter) break;
            if (kt == KeyType.Backspace && sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            } else if (key.getCharacter() != null && !Character.isISOControl(key.getCharacter())) {
                sb.append(key.getCharacter());
            }
            uiMenu.redrawInputLine(prompt, "*".repeat(sb.length()));
        }
        return sb.toString();
    }

    public boolean loginSuccess(Role role, List<String> opzioni, int selezione) throws IOException{

        if(login(role, opzioni, selezione)!= null){
            return true;
        }
        return false;

    }
}
