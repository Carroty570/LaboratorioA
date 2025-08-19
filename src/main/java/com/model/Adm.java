package com.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class Adm extends Users {

    private final List<Restaurant> restaurants = new ArrayList<>();

    public Adm(String name, String email, String passwordHash) {

        super(name, email, passwordHash, Role.ADMIN);
    }

    //Costruttore
    public Restaurant createRestaurant(String name, String address) {

        Restaurant r = new Restaurant(name, address);
        restaurants.add(r);
        return r;
    }

    public boolean removeRestaurantById(int restaurantId) {

        return restaurants.removeIf(r -> r.getId() == restaurantId);
    }

    //getters setters toggle
    public List<Restaurant> getRestaurants() {

        return Collections.unmodifiableList(restaurants);
    }

    public void toggleDelivery(int restaurantId, boolean enabled) {

        Restaurant r = requireRestaurant(restaurantId);
        r.setDelivery(enabled);
    }

    public void toggleOnlineReservation(int restaurantId, boolean enabled) {

        Restaurant r = requireRestaurant(restaurantId);
        r.setOnlineReservation(enabled);
    }

    public void replyToFeedback(int restaurantId, int feedbackId, String reply) {

        Restaurant r = requireRestaurant(restaurantId);
        r.replyToFeedback(feedbackId, reply);
    }

    //Trovare ristorante per ID
    private Restaurant requireRestaurant(int id) {

        return restaurants.stream()

                .filter(r -> r.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Ristorante non trovato: " + id));
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