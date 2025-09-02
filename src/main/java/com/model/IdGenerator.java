package com.model;

import java.util.concurrent.atomic.AtomicInteger;


public final class IdGenerator {

    // Generatori di ID per utenti, ristoranti e feedback
    private static final AtomicInteger USER_IDS = new AtomicInteger(1);
    private static final AtomicInteger REST_IDS = new AtomicInteger(1);
    private static final AtomicInteger FEEDBACK_IDS = new AtomicInteger(1);

    private IdGenerator() {

     }

    // Metodi per generare ID unici
    public static int nextUserId() { 

        return USER_IDS.getAndIncrement(); 
    }

    public static int nextRestaurantId() { 

        return REST_IDS.getAndIncrement(); 
    }

    public static int nextFeedbackId() { 

        return FEEDBACK_IDS.getAndIncrement(); 
    }
}