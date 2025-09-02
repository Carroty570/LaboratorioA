package com.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class Adm extends Users {

    private final List<Restaurant> restaurants = new ArrayList<>();
    private final String id;
    

    public Adm(String name, String email, String passwordHash, String id) {

        super(name, email, passwordHash, Role.ADMIN);
        this.id = id;
    }

    public String getStringId() {
        return id;
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