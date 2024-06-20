package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;

public interface UserStorage {
    Map<Long, User> getAll();

    User create(User user);

    User getById(Long id);

    User update(User newUser);

    void remove(User user);

    long getNextId();
}
