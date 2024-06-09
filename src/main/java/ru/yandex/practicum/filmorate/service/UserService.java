package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserService {
    void addFriend(Long id, Long friendId);

    void removeFriend(Long id, Long friendId);

    List<User> getCollectiveFriends(Long id, Long friendId);

    User getById(Long id);

    List<User> getAllFriends(Long id);

    User create(User user);

    User update(User newUser);

    Collection<User> getAllValues();

    void remove(User user);

    long getNextId();
}
