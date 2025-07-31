package com.view;

import java.util.Scanner;
import com.controller.UIController;

public class UIMenu {

    private Scanner scanner;
    private UIController controller;

    public UIMenu() {
        this.scanner = new Scanner(System.in);
        this.controller = new UIController();
    }

    public void start() {
        int choice = -1;
        while (choice != 0) {
            System.out.println("\n=== MENU PRINCIPALE ===");
            System.out.println("1. Accedi come Guest");
            System.out.println("2. Login");
            System.out.println("3. Registrati");
            System.out.println("0. Esci");
            System.out.print("Scelta: ");

            try {
                choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        controller.joinAsGuest();
                        break;
                    case 2:
                        login();
                        break;
                    case 3:
                        register();
                        break;
                    case 0:
                        System.out.println("Uscita in corso...");
                        break;
                    default:
                        System.out.println("Scelta non valida. Riprova.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Input non valido. Inserisci un numero.");
            }
        }
    }

    private void login() {
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        controller.login(email, password);
    }

    private void register() {
        System.out.println("1. Registrati come Cliente");
        System.out.println("2. Registrati come Admin");
        System.out.print("Scelta: ");
        int type = Integer.parseInt(scanner.nextLine());

        System.out.print("Nome: ");
        String name = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        if (type == 1) {
            controller.registerClient(name, email, password);
        } else if (type == 2) {
            controller.registerAdmin(name, email, password);
        } else {
            System.out.println("Tipo non valido.");
        }
    }
}
