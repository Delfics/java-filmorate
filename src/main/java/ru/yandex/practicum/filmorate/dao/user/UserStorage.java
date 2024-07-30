package ru.yandex.practicum.filmorate.dao.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getAll();

    User getById(Long id);

    User create(User user);

    User update(User newUser);

    boolean deleteById(Long id);

    User addFriend(Long userId, Long friendId);

    boolean deleteFriend(Long userId, Long friendId);

    void confirmFriend(Long userId, Long friendId);
}
