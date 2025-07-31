package com.model;

public class Restaurant {

    private String restName;
    private String position;
    private int maxPrice;
    private int minPrice;
    private String kitchenType;
    private int restID;
    private boolean onlineRes;
    private boolean delivery;

    
    public Restaurant(String restName, String position, int minPrice, int maxPrice, String kitchenType, boolean onlineRes, boolean delivery) {

        this.restName = restName;
        this.position = position;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.kitchenType = kitchenType;
        this.onlineRes = onlineRes;
        this.delivery = delivery;

       // createID(restName, position); //CreateID
    }

    
    public double calcAveragePrice() {

        return (minPrice + maxPrice) / 2.0;
    }

    
   /*  public int getID(String restName, String position) {

        
    }

    
    public void createID(String restName, String position) {

        
    }
    */

    // Getters e Setters
    public String getRestName() {

        return restName;
    }

    public void setRestName(String restName) {

        this.restName = restName;
    }

    public String getPosition() {

        return position;
    }

    public void setPosition(String position) {

        this.position = position;
    }

    public int getMaxPrice() {

        return maxPrice;
    }

    public void setMaxPrice(int maxPrice) {

        this.maxPrice = maxPrice;
    }

    public int getMinPrice() {

        return minPrice;
    }

    public void setMinPrice(int minPrice) {

        this.minPrice = minPrice;
    }

    public String getKitchenType() {

        return kitchenType;
    }

    public void setKitchenType(String kitchenType) {

        this.kitchenType = kitchenType;
    }

    public int getRestID() {

        return restID;
    }

    public boolean isOnlineRes() {

        return onlineRes;
    }

    public void setOnlineRes(boolean onlineRes) {

        this.onlineRes = onlineRes;
    }

    public boolean isDelivery() {

        return delivery;
    }

    public void setDelivery(boolean delivery) {

        this.delivery = delivery;
    }

}
