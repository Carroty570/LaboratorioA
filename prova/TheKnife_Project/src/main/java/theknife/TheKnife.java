package theknife;

import theknife.controller.RestaurantController;
import theknife.controller.UserController;
import theknife.model.*;
import theknife.repo.*;
import theknife.service.RestaurantService;
import theknife.service.UserService;
import theknife.util.ConsoleUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TheKnife {

    public static void main(String[] args) throws Exception {
        File dataDir = new File("data");
        DataStore ds = new DataStore(dataDir);
        UserService userService = new UserService(new UserRepository(ds));
        RestaurantService restaurantService = new RestaurantService(new RestaurantRepository(ds), new ReviewRepository(ds), new FavoritesRepository(ds));
        UserController userController = new UserController(userService);
        RestaurantController restaurantController = new RestaurantController(restaurantService);

        System.out.println("=== TheKnife ===");
        while (true) {
            System.out.println("\n1) Login\n2) Registrati\n3) Continua come Guest\n0) Esci");
            int c = ConsoleUtils.readInt("Scelta: ", 0, 3);
            if (c == 0) break;
            if (c == 1) {
                User u = userController.loginFlow();
                if (u != null) loggedMenu(u, restaurantController, restaurantService);
            } else if (c == 2) {
                try {
                    userController.registerFlow();
                } catch (IOException e) {
                    System.out.println("Errore di registrazione: " + e.getMessage());
                }
            } else {
                guestMenu(restaurantController);
            }
        }
        System.out.println("Arrivederci!");
    }

    private static void guestMenu(RestaurantController rc) {
        System.out.println("--- Modalità Guest ---");
        rc.searchFlow();
    }

    private static void loggedMenu(User u, RestaurantController rc, RestaurantService rs) {
        while (true) {
            System.out.println("\nBenvenuto, " + u.getUsername() + " [" + u.getRole() + "]");
            System.out.println("1) Cerca ristoranti");
            if (u.getRole() == Role.CLIENTE) {
                System.out.println("2) Preferiti");
                System.out.println("3) Aggiungi recensione");
                System.out.println("4) Modifica recensione");
                System.out.println("5) Elimina recensione");
                System.out.println("0) Logout");
                int c = theknife.util.ConsoleUtils.readInt("Scelta: ", 0, 5);
                try {
                    switch (c) {
                        case 1 -> rc.searchFlow();
                        case 2 -> rc.favoritesFlow(u);
                        case 3 -> rc.addReviewFlow(u);
                        case 4 -> rc.editReviewFlow(u);
                        case 5 -> rc.deleteReviewFlow(u);
                        case 0 -> { return; }
                    }
                } catch (IOException e) {
                    System.out.println("Errore: " + e.getMessage());
                }
            } else {
                System.out.println("2) Inserisci nuovo ristorante");
                System.out.println("3) Riepilogo ristoranti e recensioni");
                System.out.println("4) Rispondi a una recensione");
                System.out.println("0) Logout");
                int c = theknife.util.ConsoleUtils.readInt("Scelta: ", 0, 4);
                try {
                    switch (c) {
                        case 1 -> rc.searchFlow();
                        case 2 -> rc.addRestaurantFlow(u);
                        case 3 -> rc.ownerSummary(u);
                        case 4 -> rc.replyReviewFlow(u);
                        case 0 -> { return; }
                    }
                } catch (IOException e) {
                    System.out.println("Errore: " + e.getMessage());
                }
            }
        }
    }
}
