package theknife.model;

import java.util.ArrayList;
import java.util.List;

public class Restaurant {
    private String id;
    private String name;
    private Location location;
    private double averagePrice;
    private boolean delivery;
    private boolean booking;
    private String cuisine;
    private String ownerUsername;

    private final List<Review> reviews = new ArrayList<>();

    public Restaurant(String id, String name, Location location, double averagePrice, boolean delivery, boolean booking, String cuisine, String ownerUsername) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.averagePrice = averagePrice;
        this.delivery = delivery;
        this.booking = booking;
        this.cuisine = cuisine;
        this.ownerUsername = ownerUsername;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Location getLocation() { return location; }
    public double getAveragePrice() { return averagePrice; }
    public boolean isDelivery() { return delivery; }
    public boolean isBooking() { return booking; }
    public String getCuisine() { return cuisine; }
    public String getOwnerUsername() { return ownerUsername; }

    public List<Review> getReviews() { return reviews; }

    public double getAverageStars() {
        if (reviews.isEmpty()) return 0.0;
        int sum = 0;
        for (Review r : reviews) sum += r.getStars();
        return (double) sum / reviews.size();
    }

    public void addReview(Review r) { reviews.add(r); }
    public void removeReview(String reviewId) {
        reviews.removeIf(r -> r.getId().equals(reviewId));
    }

    @Override
    public String toString() {
        return String.format("%s [%s] • %s • €%.2f • delivery:%s • booking:%s • stelle medie: %.2f",
            name, cuisine, location.toString(), averagePrice, delivery?"si":"no", booking?"si":"no", getAverageStars());
    }
}
