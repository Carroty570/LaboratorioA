package theknife.repo;

import theknife.util.CsvUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class FavoritesRepository {
    private final DataStore store;

    public FavoritesRepository(DataStore store) { this.store = store; }

    public Map<String, Set<String>> loadAll() throws IOException {
        Map<String, Set<String>> map = new HashMap<>();
        File f = store.favoritesFile();
        for (String[] r : CsvUtils.readAll(f)) {
            // username,restaurantId
            map.computeIfAbsent(r[0], k -> new HashSet<>()).add(r[1]);
        }
        return map;
    }

    public void saveAll(Map<String, Set<String>> favorites) throws IOException {
        List<String[]> rows = new ArrayList<>();
        for (Map.Entry<String, Set<String>> e : favorites.entrySet()) {
            for (String restId : e.getValue()) {
                rows.add(new String[]{e.getKey(), restId});
            }
        }
        String header = "username,restaurantId";
        CsvUtils.writeAll(store.favoritesFile(), header, rows);
    }
}
