package com.model;

import java.time.LocalDateTime;


public class Feedback {

    private final int id;
    private final int authorUserId;
    private final String authorName;
    private final int restaurantId;
    private int stars; 
    private String comment;
    private String response; 
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Feedback(int authorUserId, String authorName, int restaurantId, int stars, String comment) {

        this.id = IdGenerator.nextFeedbackId();
        this.authorUserId = authorUserId;
        this.authorName = Users.requireNonBlank(authorName, "Nome autore");
        this.restaurantId = restaurantId;
        setStars(stars);
        setComment(comment);
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    // Getters and Setters
    public int getId() {

         return id; 
        }

    public int getAuthorUserId() { 

        return authorUserId; 
        }

    public String getAuthorName() { 

        return authorName; 
        }

    public int getRestaurantId() { 

        return restaurantId; 
        } 

    public int getStars() { 

        return stars; 
        }

    // Imposta le stelle e aggiorna il timestamp
    public void setStars(int stars) {

        if (stars < 1 || stars > 5) {

            throw new IllegalArgumentException("Stelle devono essere tra 1 e 5");
        }

        this.stars = stars;
        this.updatedAt = LocalDateTime.now();
    }

    public String getComment() { 

        return comment; 
        }
      
    // Imposta il commento e aggiorna il timestamp    
    public void setComment(String comment) {

        this.comment = Users.requireNonBlank(comment, "commento");
        this.updatedAt = LocalDateTime.now();
    }

    public String getResponse() { 

        return response; 
    }

    // Imposta la risposta e aggiorna il timestamp
    public void setResponse(String response) {

        this.response = response == null ? null : response.trim();
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() { 

        return createdAt; 
    }

    public LocalDateTime getUpdatedAt() { 

        return updatedAt; 
    }

    // Override toString per una rappresentazione leggibile
    @Override
    public String toString() {
        
    return "Feedback{" +
            "id=" + id +
            ", authorName='" + authorName + '\'' +
            ", restaurantId=" + restaurantId +
            ", stars=" + stars +
            ", comment='" + comment + '\'' +
            (response != null ? ", response='" + response + '\'' : "") +
            '}';
  }

}