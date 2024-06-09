package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class UserServiceImplTest {
    private final UserStorage inMemoryUserStorage = new InMemoryUserStorage();
    private final UserService userServiceImpl = new UserServiceImpl(inMemoryUserStorage);

    @Test
    void shouldCreate() {
        User user = new User();
        user.setLogin("LoginTest");
        user.setBirthday(LocalDate.of(1995, 12, 20));
        user.setEmail("TestLogin1995@gmail.com");
        user.setName("NameTest");

        userServiceImpl.create(user);

        assertEquals(user, inMemoryUserStorage.getAll().get(user.getId()), "Пользоватеь был создан");
    }

    @Test
    void shouldRemove() {
        User user = new User();
        user.setLogin("LoginTest");
        user.setBirthday(LocalDate.of(1995, 12, 20));
        user.setEmail("TestLogin1995@gmail.com");
        user.setName("NameTest");

        userServiceImpl.create(user);
        userServiceImpl.remove(user);

        assertNotEquals(user, inMemoryUserStorage.getAll().get(user.getId()), "Пользователь был удален");
    }

    @Test
    void shouldUpdate() {
        User user = new User();
        user.setLogin("LoginTest");
        user.setBirthday(LocalDate.of(1995, 12, 20));
        user.setEmail("TestLogin1995@gmail.com");
        user.setName("NameTest");

        userServiceImpl.create(user);

        user.setLogin("LoginTest1");
        user.setBirthday(LocalDate.of(1995, 12, 21));
        user.setEmail("TestLogin1994@gmail.com");
        user.setName("NameTest1");

        userServiceImpl.update(user);

        assertEquals(user, inMemoryUserStorage.getAll().get(user.getId()), "Обновили данные ");
    }

    @Test
    void shouldUpdateThrowsNotFoundException() {
        User user = new User();
        user.setLogin("LoginTest");
        user.setBirthday(LocalDate.of(1995, 12, 20));
        user.setEmail("TestLogin1995@gmail.com");
        user.setName("NameTest");

        NotFoundException thrown = assertThrows(NotFoundException.class,
                () -> userServiceImpl.update(user), "Не содержит данный id");

        assertTrue(thrown.getMessage().contains("Не содержит данный id"));
    }

    @Test
    void shouldGetById() {
        User user = new User();
        user.setLogin("LoginTest");
        user.setBirthday(LocalDate.of(1995, 12, 20));
        user.setEmail("TestLogin1995@gmail.com");
        user.setName("NameTest");

        userServiceImpl.create(user);
        User byId = userServiceImpl.getById(user.getId());

        assertEquals(user, byId, "Объекты идентичны");
    }

    @Test
    void shouldGetByIdThrowsNotFoundException() {
        User user = new User();
        NotFoundException thrown = assertThrows(NotFoundException.class,
                () -> userServiceImpl.getById(user.getId()), "Пользователь не найден с таким id "
                        + user.getId());

        assertTrue(thrown.getMessage().contains("Пользователь не найден с таким id " + user.getId()));
    }

    @Test
    void shouldAddFriend() {
        User user = new User();
        user.setLogin("LoginTest");
        user.setBirthday(LocalDate.of(1995, 12, 20));
        user.setEmail("TestLogin1995@gmail.com");
        user.setName("NameTest");

        User user1 = new User();
        user1.setLogin("LoginTest1");
        user1.setBirthday(LocalDate.of(1992, 12, 20));
        user1.setEmail("TestLogin1992@gmail.com");
        user1.setName("NameTest1");

        userServiceImpl.create(user);
        userServiceImpl.create(user1);

        userServiceImpl.addFriend(user.getId(), user1.getId());

        assertEquals(user.getFriends().contains(user1.getId()), user1.getFriends().contains(user.getId()),
                "Содержат id друг друга в друзьях");
    }


    @Test
    void shouldRemoveFriend() {
        User user = new User();
        user.setLogin("LoginTest");
        user.setBirthday(LocalDate.of(1995, 12, 20));
        user.setEmail("TestLogin1995@gmail.com");
        user.setName("NameTest");

        User user1 = new User();
        user1.setLogin("LoginTest1");
        user1.setBirthday(LocalDate.of(1992, 12, 20));
        user1.setEmail("TestLogin1992@gmail.com");
        user1.setName("NameTest1");

        userServiceImpl.create(user);
        userServiceImpl.create(user1);

        userServiceImpl.addFriend(user.getId(), user1.getId());
        userServiceImpl.removeFriend(user.getId(), user1.getId());

        assertTrue(user.getFriends().isEmpty(), "Список друзей user пуст");
        assertTrue(user1.getFriends().isEmpty(), "Список друзей user1 пуст");
    }

    @Test
    void shouldGetAllFriends() {
        User user = new User();
        user.setLogin("LoginTest");
        user.setBirthday(LocalDate.of(1995, 12, 20));
        user.setEmail("TestLogin1995@gmail.com");
        user.setName("NameTest");

        User user1 = new User();
        user1.setLogin("LoginTest1");
        user1.setBirthday(LocalDate.of(1992, 12, 20));
        user1.setEmail("TestLogin1992@gmail.com");
        user1.setName("NameTest1");

        User user2 = new User();
        user2.setLogin("LoginTest2");
        user2.setBirthday(LocalDate.of(1991, 12, 20));
        user2.setEmail("TestLogin1991@gmail.com");
        user2.setName("NameTest2");

        userServiceImpl.create(user);
        userServiceImpl.create(user1);
        userServiceImpl.create(user2);

        userServiceImpl.addFriend(user.getId(), user1.getId());
        userServiceImpl.addFriend(user.getId(), user2.getId());

        List<User> addedFriends = List.of(user1, user2);

        List<User> allFriends = userServiceImpl.getAllFriends(user.getId());

        assertEquals(addedFriends, allFriends, "Добавленные друзья идентичны");

    }

    @Test
    void shouldGetAllFriendsNotFoundException() {
        User user = new User();
        NotFoundException thrown = assertThrows(NotFoundException.class,
                () -> userServiceImpl.getAllFriends(user.getId()), "Пользователь не найден с таким id "
                        + user.getId());

        assertTrue(thrown.getMessage().contains("Пользователь не найден с таким id " + user.getId()));
    }

    @Test
    void shouldGetCollectiveFriends() {
        User user = new User();
        user.setLogin("LoginTest");
        user.setBirthday(LocalDate.of(1995, 12, 20));
        user.setEmail("TestLogin1995@gmail.com");
        user.setName("NameTest");

        User user1 = new User();
        user1.setLogin("LoginTest1");
        user1.setBirthday(LocalDate.of(1992, 12, 20));
        user1.setEmail("TestLogin1992@gmail.com");
        user1.setName("NameTest1");

        User userGlobal = new User();
        userGlobal.setLogin("LoginTest2");
        userGlobal.setBirthday(LocalDate.of(1991, 12, 20));
        userGlobal.setEmail("TestLogin1991@gmail.com");
        userGlobal.setName("NameTest2");

        userServiceImpl.create(user);
        userServiceImpl.create(user1);
        userServiceImpl.create(userGlobal);

        userServiceImpl.addFriend(user.getId(), userGlobal.getId());
        userServiceImpl.addFriend(user1.getId(), userGlobal.getId());

        List<User> collectiveFriends = userServiceImpl.getCollectiveFriends(user.getId(), user.getId());

        assertTrue(collectiveFriends.contains(userGlobal), "Содержит общего друга");

        userServiceImpl.remove(userGlobal);

        List<User> collectiveFriends1 = userServiceImpl.getCollectiveFriends(user.getId(), user.getId());

        assertNotEquals(collectiveFriends, collectiveFriends1, "Не Содержит общего друга");
    }

    @Test
    void shouldThrowNotFoundIfGetAllIsEmptyAddFriend() {
        User user = new User();
        User user1 = new User();

        NotFoundException thrown = assertThrows(NotFoundException.class,
                () -> userServiceImpl.addFriend(user.getId(), user1.getId()),
                "Список пользователей пуст, создайте пользователей");

        assertTrue(thrown.getMessage().contains("Список пользователей пуст, создайте пользователей"));
    }

    @Test
    void shouldThrowNotFoundIfUserOrUser1NotExistAddFriend() {
        User user = new User();
        User user1 = new User();
        user1.setLogin("LoginTest1");
        user1.setBirthday(LocalDate.of(1992, 12, 20));
        user1.setEmail("TestLogin1992@gmail.com");
        user1.setName("NameTest1");

        userServiceImpl.create(user1);

        NotFoundException thrown = assertThrows(NotFoundException.class,
                () -> userServiceImpl.addFriend(user.getId(),
                        user1.getId()),
                "Пользователь с таким " + user.getId() + " не найден");

        assertTrue(thrown.getMessage().contains("Пользователь с таким " + user.getId() + " не найден"));

        User user2 = new User();
        User user3 = new User();
        user3.setLogin("LoginTest1");
        user3.setBirthday(LocalDate.of(1992, 12, 20));
        user3.setEmail("TestLogin1992@gmail.com");
        user3.setName("NameTest1");

        userServiceImpl.create(user3);

        NotFoundException thrown1 = assertThrows(NotFoundException.class,
                () -> userServiceImpl.addFriend(user3.getId(),
                        user2.getId()),
                "Пользователь с таким " + user2.getId() + " не найден");

        assertTrue(thrown1.getMessage().contains("Пользователь с таким " + user2.getId() + " не найден"));
    }
}