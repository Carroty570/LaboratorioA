package theknife.repo;

import theknife.model.Location;
import theknife.model.Restaurant;
import theknife.util.CsvUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RestaurantRepository {
    private final DataStore store;

    public RestaurantRepository(DataStore store) { this.store = store; }

    public List<Restaurant> loadAll() throws IOException {
        List<Restaurant> list = new ArrayList<>();
        File f = store.restaurantsFile();
        for (String[] r : CsvUtils.readAll(f)) {
            // id,name,country,city,address,lat,lon,avgPrice,delivery,booking,cuisine,ownerUsername
            String id = r[0];
            String name = r[1];
            String country = r[2];
            String city = r[3];
            String address = r[4];
            double lat = r[5].isEmpty()?0:Double.parseDouble(r[5]);
            double lon = r[6].isEmpty()?0:Double.parseDouble(r[6]);
            double avg = r[7].isEmpty()?0:Double.parseDouble(r[7]);
            boolean del = Boolean.parseBoolean(r[8]);
            boolean book = Boolean.parseBoolean(r[9]);
            String cuisine = r[10];
            String owner = r[11];
            Restaurant restaurant = new Restaurant(id, name, new Location(country, city, address, lat, lon), avg, del, book, cuisine, owner);
            list.add(restaurant);
        }
        return list;
    }

    public void saveAll(List<Restaurant> restaurants) throws IOException {
        List<String[]> rows = new ArrayList<>();
        for (Restaurant r : restaurants) {
            rows.add(new String[]{
                r.getId(), r.getName(), r.getLocation().getCountry(), r.getLocation().getCity(),
                r.getLocation().getAddress(), String.valueOf(r.getLocation().getLatitude()),
                String.valueOf(r.getLocation().getLongitude()), String.valueOf(r.getAveragePrice()),
                String.valueOf(r.isDelivery()), String.valueOf(r.isBooking()), r.getCuisine(), r.getOwnerUsername()
            });
        }
        String header = "id,name,country,city,address,lat,lon,avgPrice,delivery,booking,cuisine,ownerUsername";
        CsvUtils.writeAll(store.restaurantsFile(), header, rows);
    }

    public static String newId() { return UUID.randomUUID().toString(); }
}
