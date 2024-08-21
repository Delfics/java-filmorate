package ru.yandex.practicum.filmorate.dao.user;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

@Repository
public interface UserStorage {
    List<User> getAll();

    User getById(Long id);

    User create(User user);

    User update(User newUser);

    boolean deleteById(Long id);

    User addFriend(Long userId, Long friendId);

    boolean deleteFriend(Long userId, Long friendId);

    Friend confirmFriend(Long userId, Long friendId);

    Set<Friend> getFriends(Long userId);

    Set<User> getFriendsUsers(Long userId);

    boolean userExists(Long userId);

    Set<User> getCollectiveFriends(Long userId, Long friendId);
}
