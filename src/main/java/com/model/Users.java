package com.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.regex.Pattern;


public abstract class Users {

   private static final Pattern EMAIL_RX = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");


    protected final int id;
    protected String name;
    protected String email;
    protected String passwordHash; 
    protected final LocalDateTime createdAt;
    protected Role role;

    protected Users(String name, String email, String passwordHash, Role role) {

        this.id = IdGenerator.nextUserId();
        this.name = requireNonBlank(name, "nome");

        if (email != null && !EMAIL_RX.matcher(email).matches()) {

            throw new IllegalArgumentException("Formato di email non valido");
        }
        
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = LocalDateTime.now();
        this.role = Objects.requireNonNull(role, "role");
    }

   
    public abstract void joinAsGuest();
    public abstract void lookMenu();
    public abstract void readFeedback(int feedbackID);

   // Getters and Setters
    public int getId() { 

        return id; 
    }

    public String getName() { 

        return name; 
    }
    public void setName(String name) { 

        this.name = requireNonBlank(name, "nome"); 
    }

    public String getEmail() { 

        return email; 
    }
    public void setEmail(String email) {

        if (email != null && !EMAIL_RX.matcher(email).matches()) {

            throw new IllegalArgumentException("Formato di email non valido");
        }

        this.email = email;
    }

    public String getPasswordHash() { 

        return passwordHash; 
    }

    public void setPasswordHash(String passwordHash) { 
        
        this.passwordHash = passwordHash; }

    public Role getRole() { 

        return role; 
    }

    protected void setRole(Role role) { 

        this.role = Objects.requireNonNull(role); 
    }

    public LocalDateTime getCreatedAt() { 

        return createdAt; 
    }

    // Uttilitari
    protected static String requireNonBlank(String value, String field) {

        if (value == null || value.isBlank())
         {

            throw new IllegalArgumentException("Il campo '" + field + "' non può essere vuoto");
        }

        return value;
    }

    @Override public boolean equals(Object o) {
        
        if (this == o){
            return true;
        }

        if (!(o instanceof Users other)){ 
            return false;
        }

        return id == other.id;
    }
    @Override public int hashCode() { 

        return Integer.hashCode(id); 
    }

    @Override
public String toString() {

    return "Users{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", email='" + email + '\'' +
            ", role=" + role +
            ", createdAt=" + createdAt +
            '}';
 }

}