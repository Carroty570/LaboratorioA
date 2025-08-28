package theknife.model;

public class Location {
    private String country;
    private String city;
    private String address;
    private double latitude;
    private double longitude;

    public Location(String country, String city, String address, double latitude, double longitude) {
        this.country = country;
        this.city = city;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public String getCountry() { return country; }
    public String getCity() { return city; }
    public String getAddress() { return address; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

    @Override
    public String toString() {
        return address + ", " + city + " (" + country + ") [" + latitude + "," + longitude + "]";
    }
}
