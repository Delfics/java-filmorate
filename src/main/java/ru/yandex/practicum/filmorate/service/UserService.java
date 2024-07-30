package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface UserService {

    Collection<User> getAllValues();

    User getById(Long id);

    User create(User user);

    User update(User newUser);

    void deleteById(Long id);

    List<User> getAllFriends(Long id);

    User addFriend(Long userId, Long friendId);

    void deleteFriendById(Long userId, Long friendId);

    Set<User> getCollectiveFriends(Long userId, Long otherId);

    void confirmFriend(Long userId, Long friendId);

}
