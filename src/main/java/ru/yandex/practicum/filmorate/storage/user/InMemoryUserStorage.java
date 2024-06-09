package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Map<Long, User> getAll() {
        return users;
    }

    @Override
    public User create(User user) {
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    public User getById(Long id) {
        return users.get(id);
    }

    @Override
    public User update(User newUser) {
        users.put(newUser.getId(), newUser);
        return users.get(newUser.getId());
    }

    @Override
    public void remove(User user) {
        users.remove(user.getId());
    }
}
