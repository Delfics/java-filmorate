package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserStorage userStorage;


    @Autowired
    public UserServiceImpl(UserStorage inMemoryUserStorage) {
        this.userStorage = inMemoryUserStorage;
    }

    @Override
    public void addFriend(Long id, Long friendId) {
        log.info("Начало добавление пользователей в друзья");

        throwNotFoundIfGetAllIsEmpty();
        throwNotFoundIfUserOrUser1NotExist(id, friendId);

        User gotUser1 = userStorage.getById(id);
        User gotUser2 = userStorage.getById(friendId);

        gotUser1.getFriends().add(gotUser2.getId());
        gotUser2.getFriends().add(gotUser1.getId());
        userStorage.update(gotUser1);
        userStorage.update(gotUser2);

        log.info("{} и {} стали друзьями успешно.", gotUser1.getName(), gotUser2.getName());
    }

    @Override
    public void removeFriend(Long id, Long friendId) {
        log.info("Начало удаление пользователей из друзей");

        throwNotFoundIfGetAllIsEmpty();
        throwNotFoundIfUserOrUser1NotExist(id, friendId);

        User gotUser1 = userStorage.getById(id);
        User gotUser2 = userStorage.getById(friendId);
        gotUser1.getFriends().remove(gotUser2.getId());
        gotUser2.getFriends().remove(gotUser1.getId());
        userStorage.update(gotUser1);
        userStorage.update(gotUser2);

        log.info("{} и {} больше не друзья успешно.", gotUser1.getName(), gotUser2.getName());
    }

    @Override
    public List<User> getCollectiveFriends(Long id, Long otherId) {
        log.info("Начало получение списка общих друзей");

        throwNotFoundIfGetAllIsEmpty();
        throwNotFoundIfUserOrUser1NotExist(id, otherId);

        User gotUser1 = userStorage.getById(id);
        User gotUser2 = userStorage.getById(otherId);

        List<Long> friendsIds = gotUser1.getFriends().stream()
                .filter((numb) -> gotUser2.getFriends().contains(numb))
                .toList();

        List<User> collectiveFriends = friendsIds.stream()
                .map(userStorage::getById)
                .collect(Collectors.toList());

        log.info("{} {} список общих друзей успешно получен", gotUser1.getName(), gotUser2.getName());
        return collectiveFriends;
    }

    @Override
    public User getById(Long id) {
        if (userStorage.getAll().containsKey(id)) {
            return userStorage.getAll().get(id);
        } else {
            throw new NotFoundException("Пользователь не найден с таким id " + id);
        }
    }

    @Override
    public List<User> getAllFriends(Long id) {
        log.info("Начало получение списка друзей");
        if (userStorage.getById(id) == null) {
            throw new NotFoundException("Пользователь не найден с таким id " + id);
        }

        User user = userStorage.getById(id);
        List<User> friends = user.getFriends().stream()
                .map((numb) -> userStorage.getAll().get(numb))
                .toList();

        log.info("Список друзей получен пользователя " + id);
        return friends;
    }

    @Override
    public User create(User user) {
        log.info("Начало создания пользователя - {}", user);

        ValidateUser.validate(user);
        user.setId(userStorage.getNextId());
        User user1 = userStorage.create(user);
        if (userStorage.getAll().containsKey(user1.getId())) {
            log.debug("Создание пользователя успешно");
        }
        return user1;
    }

    @Override
    public User update(User newUser) {
        log.info("Начало обновления пользователя - {}", newUser);

        ValidateUser.validate(newUser);
        if (userStorage.getAll().containsKey(newUser.getId())) {
            User update = userStorage.update(newUser);

            log.debug("Успешное обновление пользователя");
            return update;
        } else {
            throw new NotFoundException("Не содержит данный id");
        }
    }

    @Override
    public Collection<User> getAllValues() {
        return userStorage.getAll().values();
    }

    @Override
    public void remove(User user) {
        log.info("Начало удаление пользователя - {}", user);

        if (userStorage.getAll().containsKey(user.getId())) {
            userStorage.remove(user);

            log.debug("Успешное удаление пользователя");
        } else {
            throw new NotFoundException("Не содержит данный пользователя ".concat(user.toString()));
        }
    }

    private void throwNotFoundIfGetAllIsEmpty() {
        if (userStorage.getAll().isEmpty()) {
            throw new NotFoundException("Список пользователей пуст, создайте пользователей");
        }
    }

    private void throwNotFoundIfUserOrUser1NotExist(Long id, Long friendId) {
        if (!userStorage.getAll().containsKey(id)) {
            throw new NotFoundException("Пользователь с таким " + id + " не найден");
        } else if (!userStorage.getAll().containsKey(friendId)) {
            throw new NotFoundException("Пользователь с таким " + friendId + " не найден");
        }
    }
}
