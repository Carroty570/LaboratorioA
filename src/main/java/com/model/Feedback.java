package com.model;

/**
 * Model minimale allineato al CSV:
 * ID-Feedback,mail,ID-Restaurant,Stars,Comment
 */
public class Feedback {

    private final String idFeedback;     // es. FDB-000123
    private final String mail;           // email cliente (id utente)
    private final String restaurantMicId; // es. MIC-000123
    private int stars;                   // 1..5
    private String comment;              // testo libero

    public Feedback(String idFeedback, String mail, String restaurantMicId, int stars, String comment) {
        if (idFeedback == null || idFeedback.isBlank()) throw new IllegalArgumentException("ID-Feedback vuoto");
        if (mail == null || mail.isBlank()) throw new IllegalArgumentException("Mail utente vuota");
        if (restaurantMicId == null || restaurantMicId.isBlank()) throw new IllegalArgumentException("ID-Restaurant vuoto");
        setStars(stars);
        setComment(comment == null ? "" : comment.trim());
        this.idFeedback = idFeedback.trim();
        this.mail = mail.trim();
        this.restaurantMicId = restaurantMicId.trim();
    }

    public String getIdFeedback() { return idFeedback; }
    public String getMail() { return mail; }
    public String getRestaurantMicId() { return restaurantMicId; }
    public int getStars() { return stars; }
    public String getComment() { return comment; }

    public void setStars(int stars) {
        if (stars < 1 || stars > 5) throw new IllegalArgumentException("Stelle devono essere tra 1 e 5");
        this.stars = stars;
    }

    public void setComment(String comment) {
        this.comment = (comment == null) ? "" : comment.trim();
    }
}
