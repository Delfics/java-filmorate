package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.user.UserStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserStorage userDbStorage;

    @Autowired
    public UserServiceImpl(UserStorage inMemoryUserStorage) {
        this.userDbStorage = inMemoryUserStorage;
    }

    @Override
    public Collection<User> getAllValues() {
        List<User> allUsers = userDbStorage.getAll();

        allUsers.forEach(user -> {
            Set<User> allFriends = getAllFriends(user.getId());
            user.setFriends(allFriends.stream().map(User::getId).collect(Collectors.toSet()));
        });

        return allUsers;
    }

    @Override
    public User getById(Long id) {
        User user = userDbStorage.getById(id);

        Set<User> allFriends = getAllFriends(user.getId());

        user.setFriends(allFriends.stream().map(User::getId).collect(Collectors.toSet()));

        return user;
    }

    @Override
    public User create(User user) {
        log.info("Начало создания пользователя - {}", user);

        ValidateUser.validate(user);

        return userDbStorage.create(user);
    }

    @Override
    public User update(User newUser) {
        log.info("Начало обновления пользователя - {}", newUser);

        ValidateUser.validate(newUser);
        if (userDbStorage.getAll().stream().anyMatch(user -> user.getId().equals(newUser.getId()))) {
            User update = userDbStorage.update(newUser);

            log.debug("Успешное обновление пользователя");
            return update;
        } else {
            throw new NotFoundException("Не содержит данный id");
        }
    }

    @Override
    public void deleteById(Long id) {
        log.info("Начало удаление пользователя - {}", id);

        boolean result = userDbStorage.deleteById(id);

        if (result) {
            log.debug("Успешное удаление пользователя");
        } else {
            throw new NotFoundException("Не содержит данного пользователя ".concat(id.toString()));
        }
    }

    @Override
    public User addFriend(Long userId, Long friendId) {
        log.info("Начало добавление пользователей в друзья");

        User user = getById(userId);
        User friend = getById(friendId);

        if (user != null && friend != null) {
            User user1 = userDbStorage.addFriend(userId, friendId);
            confirmFriend(userId, friendId);
            log.info("{} и {} стали друзьями успешно.", user.getName(), friend.getName());

            return getById(user1.getId());
        } else {
            throw new NotFoundException("Не содержит данного пользователя ".concat(userId.toString()));
        }
    }

    @Override
    public void deleteFriendById(Long userId, Long friendId) {
        log.info("Начало удаление пользователей из друзей");

        User user = userDbStorage.getById(userId);
        User friend = userDbStorage.getById(friendId);

        if (user != null && friend != null) {
            boolean result = userDbStorage.deleteFriend(userId, friendId);

            if (result) {
                log.info("{} и {} больше не друзья успешно.", user.getName(), friend.getName());

            }
        } else {
            throw new NotFoundException("Пользователи " + user.getName() + " или " + friend.getName() + " не найдены");
        }
    }

    @Override
    public Set<User> getCollectiveFriends(Long id, Long otherId) {
        log.info("Начало получение списка общих друзей");

        Set<User> userFriends = getAllFriends(id);
        Set<User> otherFriends = getAllFriends(otherId);

        Set<User> result = new HashSet<>();
        userFriends.forEach(user -> {
            result.addAll(otherFriends.stream().filter(otherFriend ->
                            otherFriend.getId().equals(user.getId()))
                    .collect(Collectors.toSet()));
        });

        log.info("Для {} и {} список общих друзей успешно получен", id, otherId);
        return result;
    }

    @Override
    public Set<User> getAllFriends(Long id) {
        log.info("Начало получение списка друзей");
        if (userDbStorage.getById(id) == null) {
            throw new NotFoundException("Пользователь не найден с таким id " + id);
        }

        User user = userDbStorage.getById(id);

        List<Friend> friends = userDbStorage.getFriends(user.getId());

        Set<User> users = friends.stream()
                .map(friend -> userDbStorage.getById(friend.getFriendId())).collect(Collectors.toSet());


        log.info("Список друзей получен пользователя " + id);
        return users;
    }

    @Override
    public void confirmFriend(Long userId, Long friendId) {
        userDbStorage.confirmFriend(userId, friendId);
    }
}
