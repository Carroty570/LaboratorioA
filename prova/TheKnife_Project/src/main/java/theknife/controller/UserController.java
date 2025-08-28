package theknife.controller;

import theknife.model.Role;
import theknife.model.User;
import theknife.service.UserService;
import theknife.util.ConsoleUtils;

import java.io.IOException;

public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public User loginFlow() {
        String username = ConsoleUtils.readLine("Username: ");
        String password = ConsoleUtils.readLine("Password: ");
        User u = userService.login(username, password);
        if (u == null) System.out.println("Credenziali non valide.");
        return u;
    }

    public User registerFlow() throws IOException {
        String username = ConsoleUtils.readLine("Scegli username: ");
        String password = ConsoleUtils.readLine("Scegli password: ");
        String name = ConsoleUtils.readLine("Nome: ");
        String surname = ConsoleUtils.readLine("Cognome: ");
        String birthdate = ConsoleUtils.readLine("Data di nascita (yyyy-MM-dd, opzionale invio): ");
        String domicile = ConsoleUtils.readLine("Domicilio (città): ");
        System.out.println("Ruolo: [1] Cliente  [2] Ristoratore");
        int r = ConsoleUtils.readInt("Scelta: ", 1, 2);
        Role role = r == 1 ? Role.CLIENTE : Role.RISTORATORE;
        User u = userService.register(username, password, name, surname, birthdate, domicile, role);
        if (u == null) System.out.println("Username già esistente.");
        else System.out.println("Registrazione completata. Ora effettua il login.");
        return u;
    }
}
