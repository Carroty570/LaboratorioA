package theknife.service;

import theknife.model.Role;
import theknife.model.User;
import theknife.repo.UserRepository;
import theknife.util.SecurityUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class UserService {
    private final UserRepository userRepository;
    private Map<String, User> users;

    public UserService(UserRepository userRepository) throws IOException {
        this.userRepository = userRepository;
        this.users = userRepository.loadAll();
    }

    public User login(String username, String password) {
        User u = users.get(username);
        if (u == null) return null;
        String hash = SecurityUtils.sha256(password);
        if (u.getPasswordHash().equals(hash)) return u;
        return null;
    }

    public User register(String username, String password, String name, String surname, String birthdate, String domicile, Role role) throws IOException {
        if (users.containsKey(username)) return null;
        String hash = SecurityUtils.sha256(password);
        User u = new User(username, hash, name, surname, birthdate, domicile, role);
        users.put(username, u);
        userRepository.saveAll(users.values());
        return u;
    }

    public Collection<User> allUsers() { return users.values(); }
}
