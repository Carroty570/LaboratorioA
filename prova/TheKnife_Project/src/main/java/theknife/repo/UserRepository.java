package theknife.repo;

import theknife.model.Role;
import theknife.model.User;
import theknife.util.CsvUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class UserRepository {
    private final DataStore store;

    public UserRepository(DataStore store) { this.store = store; }

    public Map<String, User> loadAll() throws IOException {
        Map<String, User> map = new LinkedHashMap<>();
        for (String[] r : CsvUtils.readAll(store.usersFile())) {
            // username,passwordHash,name,surname,birthdate,domicile,role
            Role role = Role.valueOf(r[6]);
            User u = new User(r[0], r[1], r[2], r[3], r[4], r[5], role);
            map.put(u.getUsername(), u);
        }
        return map;
    }

    public void saveAll(Collection<User> users) throws IOException {
        List<String[]> rows = new ArrayList<>();
        for (User u : users) {
            rows.add(new String[]{u.getUsername(), u.getPasswordHash(), u.getName(), u.getSurname(), u.getBirthdate(), u.getDomicile(), u.getRole().name()});
        }
        String header = "username,passwordHash,name,surname,birthdate,domicile,role";
        CsvUtils.writeAll(store.usersFile(), header, rows);
    }
}
