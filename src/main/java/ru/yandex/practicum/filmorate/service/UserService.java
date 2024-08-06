package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserService {

    List<User> getAllValues();

    User getById(Long id);

    User create(User user);

    User update(User newUser);

    void deleteById(Long id);

    Set<User> getFriends(Long id);

    User addFriend(Long userId, Long friendId);

    void deleteFriendById(Long userId, Long friendId);

    Set<User> getCollectiveFriends(Long userId, Long otherId);

    Friend confirmFriend(Long userId, Long friendId);

}
