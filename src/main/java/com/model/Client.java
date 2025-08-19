package com.model;

import java.util.Objects;

public class Client extends Users {

    public Client(String name, String email, String passwordHash) {

        super(name, email, passwordHash, Role.CLIENT);
    }

    //Costruttore feedback
    public Feedback leaveFeedback(Restaurant restaurant, int stars, String comment) {

        Objects.requireNonNull(restaurant, "ristorante");
        Feedback fb = new Feedback(this.getId(), this.getName(), restaurant.getId(), stars, comment);
        restaurant.addFeedback(fb);
        return fb;
    }

    public void editOwnFeedback(Restaurant restaurant, int feedbackId, int newStars, String newComment) {

        Objects.requireNonNull(restaurant, "ristorante");
        Feedback fb = restaurant.getFeedbackById(feedbackId);

        if (fb.getAuthorUserId() != this.getId()) {
            
            throw new IllegalStateException("Il client non può modificare feedback di altri utenti");
        }

        fb.setStars(newStars);
        fb.setComment(newComment);
    }


    @Override
    public void joinAsGuest() {
        
    }

    @Override
    public void lookMenu() {
        
    }

    @Override
    public void readFeedback(int feedbackID) {
        
    }
}