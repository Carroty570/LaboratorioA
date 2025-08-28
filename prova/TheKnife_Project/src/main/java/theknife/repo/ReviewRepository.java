package theknife.repo;

import theknife.model.Review;
import theknife.util.CsvUtils;

import java.io.IOException;
import java.util.*;
import java.io.File;

public class ReviewRepository {
    private final DataStore store;

    public ReviewRepository(DataStore store) { this.store = store; }

    public List<Review> loadAll() throws IOException {
        List<Review> list = new ArrayList<>();
        File f = store.reviewsFile();
        for (String[] r : CsvUtils.readAll(f)) {
            // id,restaurantId,username,stars,text,reply
            Review rev = new Review(r[0], r[1], r[2], Integer.parseInt(r[3]), r[4], r.length>5?r[5]:"");
            list.add(rev);
        }
        return list;
    }

    public void saveAll(List<Review> reviews) throws IOException {
        List<String[]> rows = new ArrayList<>();
        for (Review r : reviews) {
            rows.add(new String[]{r.getId(), r.getRestaurantId(), r.getUsername(), String.valueOf(r.getStars()), r.getText(), r.getOwnerReply()==null? "": r.getOwnerReply()});
        }
        String header = "id,restaurantId,username,stars,text,reply";
        CsvUtils.writeAll(store.reviewsFile(), header, rows);
    }

    public static String newId() { return UUID.randomUUID().toString(); }
}
