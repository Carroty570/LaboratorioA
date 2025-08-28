package theknife.service;

import theknife.model.Restaurant;
import theknife.model.Review;
import theknife.model.User;
import theknife.repo.FavoritesRepository;
import theknife.repo.RestaurantRepository;
import theknife.repo.ReviewRepository;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final ReviewRepository reviewRepository;
    private final FavoritesRepository favoritesRepository;

    private List<Restaurant> restaurants;
    private List<Review> reviews;
    private Map<String, Set<String>> favorites;

    public RestaurantService(RestaurantRepository restaurantRepository, ReviewRepository reviewRepository, FavoritesRepository favoritesRepository) throws IOException {
        this.restaurantRepository = restaurantRepository;
        this.reviewRepository = reviewRepository;
        this.favoritesRepository = favoritesRepository;
        this.restaurants = restaurantRepository.loadAll();
        this.reviews = reviewRepository.loadAll();
        this.favorites = favoritesRepository.loadAll();
        linkReviews();
    }

    private void linkReviews() {
        Map<String, List<Review>> byRest = reviews.stream().collect(Collectors.groupingBy(Review::getRestaurantId));
        for (Restaurant r : restaurants) {
            r.getReviews().clear();
            List<Review> list = byRest.getOrDefault(r.getId(), Collections.emptyList());
            r.getReviews().addAll(list);
        }
    }

    public List<Restaurant> search(String city, String cuisine, Double priceMin, Double priceMax, Boolean delivery, Boolean booking, Double minStars) {
        return restaurants.stream().filter(r -> {
            if (city != null && !city.isBlank() && !r.getLocation().getCity().equalsIgnoreCase(city)) return false;
            if (cuisine != null && !cuisine.isBlank() && !r.getCuisine().equalsIgnoreCase(cuisine)) return false;
            if (priceMin != null && r.getAveragePrice() < priceMin) return false;
            if (priceMax != null && r.getAveragePrice() > priceMax) return false;
            if (delivery != null && r.isDelivery() != delivery) return false;
            if (booking != null && r.isBooking() != booking) return false;
            if (minStars != null && r.getAverageStars() < minStars) return false;
            return true;
        }).collect(Collectors.toList());
    }

    public Restaurant byId(String id) {
        for (Restaurant r : restaurants) if (r.getId().equals(id)) return r;
        return null;
    }

    public Restaurant addRestaurant(User owner, Restaurant r) throws IOException {
        if (!owner.isRistoratore()) throw new IllegalArgumentException("Solo i ristoratori possono inserire ristoranti.");
        List<Restaurant> toSave = new ArrayList<>(restaurants);
        toSave.add(r);
        restaurantRepository.saveAll(toSave);
        restaurants = toSave;
        return r;
    }

    public Review addReview(User user, String restaurantId, int stars, String text) throws IOException {
        if (stars < 1 || stars > 5) throw new IllegalArgumentException("Stelle 1..5");
        String id = ReviewRepository.newId();
        Review rev = new Review(id, restaurantId, user.getUsername(), stars, text, "");
        List<Review> toSave = new ArrayList<>(reviews);
        toSave.add(rev);
        reviewRepository.saveAll(toSave);
        reviews = toSave;
        linkReviews();
        return rev;
    }

    public boolean editReview(User user, String reviewId, int newStars, String newText) throws IOException {
        boolean changed = false;
        for (Review r : reviews) {
            if (r.getId().equals(reviewId) && r.getUsername().equals(user.getUsername())) {
                r.setStars(newStars);
                r.setText(newText);
                changed = true;
                break;
            }
        }
        if (changed) {
            reviewRepository.saveAll(reviews);
            linkReviews();
        }
        return changed;
    }

    public boolean deleteReview(User user, String reviewId) throws IOException {
        boolean removed = reviews.removeIf(r -> r.getId().equals(reviewId) && r.getUsername().equals(user.getUsername()));
        if (removed) {
            reviewRepository.saveAll(reviews);
            linkReviews();
        }
        return removed;
    }

    public boolean replyToReview(User owner, String reviewId, String reply) throws IOException {
        for (Review r : reviews) {
            Restaurant rest = byId(r.getRestaurantId());
            if (r.getId().equals(reviewId) && rest != null && rest.getOwnerUsername().equals(owner.getUsername())) {
                if (r.getOwnerReply() != null && !r.getOwnerReply().isBlank()) return false;
                r.setOwnerReply(reply);
                reviewRepository.saveAll(reviews);
                linkReviews();
                return true;
            }
        }
        return false;
    }

    public Map<String, Object> summaryForOwner(User owner) {
        Map<String, Object> m = new LinkedHashMap<>();
        List<Restaurant> mine = restaurants.stream().filter(r -> r.getOwnerUsername().equals(owner.getUsername())).collect(Collectors.toList());
        m.put("count", mine.size());
        double avg = 0.0;
        int nrev = 0;
        for (Restaurant r : mine) {
            avg += r.getAverageStars();
            nrev += r.getReviews().size();
        }
        if (!mine.isEmpty()) avg /= mine.size();
        m.put("avgStars", mine.isEmpty()?0.0:avg);
        m.put("reviews", nrev);
        return m;
    }

    public Set<String> favoritesOf(String username) {
        return favorites.getOrDefault(username, new HashSet<>());
    }

    public void addFavorite(String username, String restaurantId) throws IOException {
        Set<String> set = favorites.computeIfAbsent(username, k -> new HashSet<>());
        set.add(restaurantId);
        favoritesRepository.saveAll(favorites);
    }

    public void removeFavorite(String username, String restaurantId) throws IOException {
        Set<String> set = favorites.computeIfAbsent(username, k -> new HashSet<>());
        set.remove(restaurantId);
        favoritesRepository.saveAll(favorites);
    }

    public String newRestaurantId() {
        return theknife.repo.RestaurantRepository.newId();
    }

    public List<Restaurant> getAll() { return restaurants; }
}
