package theknife.controller;

import theknife.model.Location;
import theknife.model.Restaurant;
import theknife.model.Review;
import theknife.model.User;
import theknife.service.RestaurantService;
import theknife.util.ConsoleUtils;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class RestaurantController {

    private final RestaurantService service;

    public RestaurantController(RestaurantService service) {
        this.service = service;
    }

    public void searchFlow() {
        String city = ConsoleUtils.readLine("Città (obbligatoria per la ricerca, lascia vuoto per tutte): ");
        String cuisine = ConsoleUtils.readLine("Tipo cucina (opzionale): ");
        Double pmin = null, pmax = null;
        String smin = ConsoleUtils.readLine("Prezzo minimo (invio per nessun vincolo): ");
        if (!smin.isBlank()) pmin = Double.parseDouble(smin.replace(',', '.'));
        String smax = ConsoleUtils.readLine("Prezzo massimo (invio per nessun vincolo): ");
        if (!smax.isBlank()) pmax = Double.parseDouble(smax.replace(',', '.'));
        Boolean delivery = null, booking = null;
        String sdel = ConsoleUtils.readLine("Delivery? (s/n/invio): ");
        if (sdel.equalsIgnoreCase("s")) delivery = true; else if (sdel.equalsIgnoreCase("n")) delivery = false;
        String sb = ConsoleUtils.readLine("Prenotazione online? (s/n/invio): ");
        if (sb.equalsIgnoreCase("s")) booking = true; else if (sb.equalsIgnoreCase("n")) booking = false;
        Double minStars = null;
        String ms = ConsoleUtils.readLine("Stelle minime (1-5, invio per nessun vincolo): ");
        if (!ms.isBlank()) minStars = Double.parseDouble(ms.replace(',', '.'));

        List<Restaurant> results = service.search(city, cuisine, pmin, pmax, delivery, booking, minStars);
        if (results.isEmpty()) {
            System.out.println("Nessun risultato.");
            return;
        }
        for (int i = 0; i < results.size(); i++) {
            System.out.printf("[%d] %s%n", i+1, results.get(i));
        }
        int sel = ConsoleUtils.readInt("Seleziona un ristorante (0 per annullare): ", 0, results.size());
        if (sel == 0) return;
        Restaurant chosen = results.get(sel-1);
        showRestaurant(chosen);
    }

    public void showRestaurant(Restaurant r) {
        System.out.println("=== Dettagli Ristorante ===");
        System.out.println(r);
        System.out.println("--- Recensioni ---");
        if (r.getReviews().isEmpty()) System.out.println("Nessuna recensione.");
        for (Review rev : r.getReviews()) {
            System.out.println(rev.toString());
        }
    }

    public void addRestaurantFlow(User owner) throws IOException {
        if (!owner.isRistoratore()) {
            System.out.println("Solo ristoratori.");
            return;
        }
        String id = service.newRestaurantId();
        String name = ConsoleUtils.readLine("Nome: ");
        String country = ConsoleUtils.readLine("Nazione: ");
        String city = ConsoleUtils.readLine("Città: ");
        String address = ConsoleUtils.readLine("Indirizzo: ");
        double lat = ConsoleUtils.readDouble("Latitudine: ", -90, 90);
        double lon = ConsoleUtils.readDouble("Longitudine: ", -180, 180);
        double price = ConsoleUtils.readDouble("Prezzo medio (€): ", 0, 1000);
        boolean del = ConsoleUtils.readYesNo("Delivery disponibile?");
        boolean book = ConsoleUtils.readYesNo("Prenotazione online disponibile?");
        String cuisine = ConsoleUtils.readLine("Tipo di cucina: ");

        Restaurant r = new Restaurant(id, name, new Location(country, city, address, lat, lon), price, del, book, cuisine, owner.getUsername());
        service.addRestaurant(owner, r);
        System.out.println("Ristorante aggiunto con ID: " + id);
    }

    public void addReviewFlow(User user) throws IOException {
        String id = ConsoleUtils.readLine("ID ristorante: ");
        int stars = ConsoleUtils.readInt("Stelle (1..5): ", 1, 5);
        String text = ConsoleUtils.readLine("Testo recensione: ");
        Review rev = service.addReview(user, id, stars, text);
        System.out.println("Recensione inserita con ID: " + rev.getId());
    }

    public void editReviewFlow(User user) throws IOException {
        String id = ConsoleUtils.readLine("ID recensione da modificare: ");
        int stars = ConsoleUtils.readInt("Nuove stelle (1..5): ", 1, 5);
        String text = ConsoleUtils.readLine("Nuovo testo: ");
        boolean ok = service.editReview(user, id, stars, text);
        System.out.println(ok ? "Recensione aggiornata." : "Impossibile aggiornare (ID inesistente o non tua).");
    }

    public void deleteReviewFlow(User user) throws IOException {
        String id = ConsoleUtils.readLine("ID recensione da cancellare: ");
        boolean ok = service.deleteReview(user, id);
        System.out.println(ok ? "Recensione cancellata." : "Impossibile cancellare (ID inesistente o non tua).");
    }

    public void replyReviewFlow(User owner) throws IOException {
        String id = ConsoleUtils.readLine("ID recensione a cui rispondere: ");
        String reply = ConsoleUtils.readLine("Risposta (una sola per recensione): ");
        boolean ok = service.replyToReview(owner, id, reply);
        System.out.println(ok ? "Risposta pubblicata." : "Impossibile rispondere (non autorizzato o già presente).");
    }

    public void favoritesFlow(User user) throws IOException {
        System.out.println("[1] Aggiungi preferito");
        System.out.println("[2] Rimuovi preferito");
        System.out.println("[3] Visualizza preferiti");
        int c = ConsoleUtils.readInt("Scelta: ", 1, 3);
        if (c == 1) {
            String id = ConsoleUtils.readLine("ID ristorante: ");
            service.addFavorite(user.getUsername(), id);
            System.out.println("Aggiunto ai preferiti.");
        } else if (c == 2) {
            String id = ConsoleUtils.readLine("ID ristorante: ");
            service.removeFavorite(user.getUsername(), id);
            System.out.println("Rimosso dai preferiti.");
        } else {
            Set<String> fav = service.favoritesOf(user.getUsername());
            if (fav.isEmpty()) System.out.println("Nessun preferito.");
            for (String id : fav) {
                var r = service.byId(id);
                System.out.println((r!=null? r.toString() : id));
            }
        }
    }

    public void ownerSummary(User owner) {
        var m = service.summaryForOwner(owner);
        System.out.printf("Ristoranti inseriti: %d, Media stelle: %.2f, Numero recensioni: %d%n",
                (int)m.get("count"), (double)m.get("avgStars"), (int)m.get("reviews"));
    }
}
