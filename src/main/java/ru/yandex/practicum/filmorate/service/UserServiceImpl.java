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

@Service
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserStorage userDbStorage;

    @Autowired
    public UserServiceImpl(UserStorage inMemoryUserStorage) {
        this.userDbStorage = inMemoryUserStorage;
    }

    @Override
    public List<User> getAllValues() {
        return userDbStorage.getAll();
    }

    @Override
    public User getById(Long id) {
        try {
            return userDbStorage.getById(id);
        } catch (RuntimeException e) {
            throw new NotFoundException(e.getMessage());
        }
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

        User update = userDbStorage.update(newUser);

        log.debug("Успешное обновление пользователя");
        return update;
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

        User userWithFriend = userDbStorage.addFriend(userId, friendId);
        log.info("{} и {} стали друзьями успешно.", userId, friendId);
        return userWithFriend;
    }

    @Override
    public void deleteFriendById(Long userId, Long friendId) {
        log.info("Начало удаление пользователей из друзей");

        userDbStorage.deleteFriend(userId, friendId);

        log.info("{} и {} больше не друзья успешно.", userId, friendId);
    }

    @Override
    public Set<User> getCollectiveFriends(Long id, Long otherId) {
        log.info("Начало получение списка общих друзей");

        Set<User> collectiveFriends = userDbStorage.getCollectiveFriends(id, otherId);

        log.info("Для {} и {} список общих друзей успешно получен", id, otherId);
        return collectiveFriends;
    }

    @Override
    public Set<User> getFriends(Long id) {
        log.info("Начало получение списка друзей");

        Set<User> users = userDbStorage.getFriendsUsers(id);

        log.info("Список друзей получен пользователя " + id);
        return users;
    }

    @Override
    public Friend confirmFriend(Long userId, Long friendId) {
        return userDbStorage.confirmFriend(userId, friendId);
    }
}
