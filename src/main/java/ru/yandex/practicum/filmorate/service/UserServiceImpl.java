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
    private final UserStorage inMemoryUserStorage;


    @Autowired
    public UserServiceImpl(UserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    @Override
    public void addFriend(Long id, Long friendId) {
        log.info("Начало добавление пользователей в друзья");

        throwNotFoundIfGetAllIsEmpty();
        throwNotFoundIfUserOrUser1NotExist(id, friendId);

        User gotUser1 = inMemoryUserStorage.getById(id);
        User gotUser2 = inMemoryUserStorage.getById(friendId);

        gotUser1.getFriends().add(gotUser2.getId());
        gotUser2.getFriends().add(gotUser1.getId());
        inMemoryUserStorage.update(gotUser1);
        inMemoryUserStorage.update(gotUser2);

        log.info(gotUser1.getName() + " и " + gotUser2.getName() + " стали друзьями успешно.");
    }

    @Override
    public void removeFriend(Long id, Long friendId) {
        log.info("Начало удаление пользователей из друзей");

        throwNotFoundIfGetAllIsEmpty();
        throwNotFoundIfUserOrUser1NotExist(id, friendId);

        User gotUser1 = inMemoryUserStorage.getById(id);
        User gotUser2 = inMemoryUserStorage.getById(friendId);
        gotUser1.getFriends().remove(gotUser2.getId());
        gotUser2.getFriends().remove(gotUser1.getId());
        inMemoryUserStorage.update(gotUser1);
        inMemoryUserStorage.update(gotUser2);

        log.info(gotUser1.getName() + " и " + gotUser2.getName() + " больше не друзья успешно.");
    }

    @Override
    public List<User> getCollectiveFriends(Long id, Long otherId) {
        log.info("Начало получение списка общих друзей");

        throwNotFoundIfGetAllIsEmpty();
        throwNotFoundIfUserOrUser1NotExist(id, otherId);

        User gotUser1 = inMemoryUserStorage.getById(id);
        User gotUser2 = inMemoryUserStorage.getById(otherId);

        List<Long> friendsIds = gotUser1.getFriends().stream()
                .filter((numb) -> gotUser2.getFriends().contains(numb))
                .toList();

        List<User> collectiveFriends = friendsIds.stream()
                .map(inMemoryUserStorage::getById)
                .collect(Collectors.toList());

        log.info(gotUser1.getName() + " " + gotUser2.getName() + " список общих друзей успешно получен");
        return collectiveFriends;
    }

    @Override
    public User getById(Long id) {
        if (inMemoryUserStorage.getAll().containsKey(id)) {
            return inMemoryUserStorage.getAll().get(id);
        } else {
            throw new NotFoundException("Пользователь не найден с таким id " + id);
        }
    }

    @Override
    public List<User> getAllFriends(Long id) {
        log.info("Начало получение списка друзей");
        if (inMemoryUserStorage.getById(id) == null) {
            throw new NotFoundException("Пользователь не найден с таким id " + id);
        }

        User user = inMemoryUserStorage.getById(id);
        List<User> friends = user.getFriends().stream()
                .map((numb) -> inMemoryUserStorage.getAll().get(numb))
                .toList();

        log.info("Список друзей получен пользователя " + id);
        return friends;
    }

    @Override
    public User create(User user) {
        log.info("Начало создания пользователя - {}", user);

        ValidateUser.validate(user);
        user.setId(getNextId());
        User user1 = inMemoryUserStorage.create(user);
        if (inMemoryUserStorage.getAll().containsKey(user1.getId())) {
            log.debug("Создание пользователя успешно");
        }
        return user1;
    }

    @Override
    public User update(User newUser) {
        log.info("Начало обновления пользователя - {}", newUser);

        ValidateUser.validate(newUser);
        if (inMemoryUserStorage.getAll().containsKey(newUser.getId())) {
            User update = inMemoryUserStorage.update(newUser);

            log.debug("Успешное обновление пользователя");
            return update;
        } else {
            throw new NotFoundException("Не содержит данный id");
        }
    }

    @Override
    public Collection<User> getAllValues() {
        return inMemoryUserStorage.getAll().values();
    }

    @Override
    public void remove(User user) {
        log.info("Начало удаление пользователя - {}", user);

        if (inMemoryUserStorage.getAll().containsKey(user.getId())) {
            inMemoryUserStorage.remove(user);

            log.debug("Успешное удаление пользователя");
        } else {
            throw new NotFoundException("Не содержит данный пользователя " + user);
        }
    }

    @Override
    public long getNextId() {
        long currentMaxId = inMemoryUserStorage.getAll().keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void throwNotFoundIfGetAllIsEmpty() {
        if (inMemoryUserStorage.getAll().isEmpty()) {
            throw new NotFoundException("Список пользователей пуст, создайте пользователей");
        }
    }

    private void throwNotFoundIfUserOrUser1NotExist(Long id, Long friendId) {
        if (!inMemoryUserStorage.getAll().containsKey(id)) {
            throw new NotFoundException("Пользователь с таким " + id + " не найден");
        } else if (!inMemoryUserStorage.getAll().containsKey(friendId)) {
            throw new NotFoundException("Пользователь с таким " + friendId + " не найден");
        }
    }
}
