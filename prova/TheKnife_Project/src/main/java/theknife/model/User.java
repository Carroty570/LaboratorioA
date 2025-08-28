package theknife.model;

public class User {
    private String username;
    private String passwordHash;
    private String name;
    private String surname;
    private String birthdate; // ISO yyyy-MM-dd
    private String domicile;
    private Role role;

    public User(String username, String passwordHash, String name, String surname, String birthdate, String domicile, Role role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.name = name;
        this.surname = surname;
        this.birthdate = birthdate;
        this.domicile = domicile;
        this.role = role;
    }

    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public String getBirthdate() { return birthdate; }
    public String getDomicile() { return domicile; }
    public Role getRole() { return role; }
    public boolean isRistoratore() { return role == Role.RISTORATORE; }
}
