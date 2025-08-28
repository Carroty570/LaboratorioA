package theknife.repo;

import java.io.File;

public class DataStore {
    private final File baseDir;

    public DataStore(File baseDir) {
        this.baseDir = baseDir;
    }

    public File restaurantsFile() { return new File(baseDir, "restaurants.csv"); }
    public File usersFile() { return new File(baseDir, "users.csv"); }
    public File reviewsFile() { return new File(baseDir, "reviews.csv"); }
    public File favoritesFile() { return new File(baseDir, "favorites.csv"); }
}
