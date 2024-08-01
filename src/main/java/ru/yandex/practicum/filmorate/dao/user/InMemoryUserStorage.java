package ru.yandex.practicum.filmorate.dao.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
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
    public boolean deleteById(Long id) {
        users.remove(id);
        return true;
    }

    @Override
    public User addFriend(Long userId, Long friendId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean deleteFriend(Long userId, Long friendId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void confirmFriend(Long userId, Long friendId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @Override
    public List<Friend> getFriends(Long userId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
