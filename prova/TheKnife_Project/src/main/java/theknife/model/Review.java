package theknife.model;

public class Review {
    private String id;
    private String restaurantId;
    private String username;
    private int stars; // 1..5
    private String text;
    private String ownerReply; // at most one reply

    public Review(String id, String restaurantId, String username, int stars, String text, String ownerReply) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.username = username;
        this.stars = stars;
        this.text = text;
        this.ownerReply = ownerReply;
    }

    public String getId() { return id; }
    public String getRestaurantId() { return restaurantId; }
    public String getUsername() { return username; }
    public int getStars() { return stars; }
    public String getText() { return text; }
    public String getOwnerReply() { return ownerReply; }
    public void setStars(int stars) { this.stars = stars; }
    public void setText(String text) { this.text = text; }
    public void setOwnerReply(String ownerReply) { this.ownerReply = ownerReply; }

    @Override
    public String toString() {
        String base = String.format("• %s — %d★: %s", username, stars, text);
        if (ownerReply != null && !ownerReply.isBlank()) {
            base += String.format("\n   ↳ Risposta del ristoratore: %s", ownerReply);
        }
        return base;
    }
}
