package com.model;

import java.util.*;


public class Restaurant {

    private final int id;
    private String name;
    private String address;
    private boolean onlineReservation;
    private boolean delivery;
    private final Map<String, Double> menu = new LinkedHashMap<>(); 
    private final List<Feedback> feedbacks = new ArrayList<>();

    public Restaurant(String name, String address) {

        this.id = IdGenerator.nextRestaurantId();
        this.name = Users.requireNonBlank(name, "name");
        this.address = Users.requireNonBlank(address, "address");
    }

    // Info getters

    public int getId() { 

        return id;
       }

    public String getName() {

        return name;
        }

    public void setName(String name) {

        this.name = Users.requireNonBlank(name, "name");
        }

    public String getAddress() {

        return address; 
        }

    public void setAddress(String address) {

        this.address = Users.requireNonBlank(address, "address"); 
        }

    // Riservazioni

    public boolean isOnlineReservation() { 

        return onlineReservation; 
        }

    public void setOnlineReservation(boolean onlineReservation) { 

        this.onlineReservation = onlineReservation; 
        }

    public boolean isDelivery() {

        return delivery; 
        }

    public void setDelivery(boolean delivery) { 
        
        this.delivery = delivery; 
        }

    // Operazioni con il menu

    public Map<String, Double> getMenu() {

        return Collections.unmodifiableMap(menu); 
    }

    public void addMenuItem(String itemName, double price) {

        if (itemName == null || itemName.isBlank()) {

        throw new IllegalArgumentException("Il nome dell'elemento del menu non può essere vuoto");
        }

        if (price < 0){
            throw new IllegalArgumentException("il prezzo non può essere negativo");
        }
        menu.put(itemName.trim(), price);
        
    }
    public void removeMenuItem(String itemName) {

        if (itemName == null) return;
        menu.remove(itemName);
    }

    // Operazioni con il feedback
    public List<Feedback> getFeedbacks() { 

        return Collections.unmodifiableList(feedbacks); 
    }

    public void addFeedback(Feedback feedback) {

        Objects.requireNonNull(feedback, "feedback");

        if (feedback.getRestaurantId() != this.id) {

            throw new IllegalArgumentException("Feedback non appartiene a questo ristorante");
        }
        feedbacks.add(feedback);
    }

    public Feedback getFeedbackById(int feedbackId) {

        return feedbacks.stream()

                .filter(f -> f.getId() == feedbackId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Feedback non trovato: " + feedbackId));
    }

    public void replyToFeedback(int feedbackId, String reply) {

        Feedback fb = getFeedbackById(feedbackId);
        fb.setResponse(reply);
    }

    public double getAverageRating() {

        if (feedbacks.isEmpty()){

             return 0.0;
        }
        
        return feedbacks.stream().mapToInt(Feedback::getStars).average().orElse(0.0);
    }

   @Override
    public String toString() {

        return "Restaurant{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", address='" + address + '\'' +
            ", onlineReservation=" + onlineReservation +
            ", delivery=" + delivery +
            ", avgRating=" + String.format(java.util.Locale.ROOT, "%.2f", getAverageRating()) +
            '}';
    }


    @Override 
    public boolean equals(Object o) {

        if (this == o){

         return true;
         }

        if (!(o instanceof Restaurant other)){

         return false;
         }

        return id == other.id;
    }
    @Override public int hashCode() {
         
        return Integer.hashCode(id); 
    }
}
