package ru.yandex.practicum.filmorate.dao.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.practicum.filmorate.dal.mappers.FriendRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.UserResultSetMapper;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.Friendship;
import ru.yandex.practicum.filmorate.service.UserServiceImpl;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@JdbcTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase
@Import({UserServiceImpl.class, UserDbStorage.class,
        UserResultSetMapper.class, FriendRowMapper.class})
class UserServiceImplTest {
    @Autowired
    private UserServiceImpl userServiceImpl;

    @Test
    void getAll() {
        long id1 = 1;
        long id2 = 2;
        List<User> values = new ArrayList<>();

        User byId = userServiceImpl.getById(id1);
        User byId1 = userServiceImpl.getById(id2);
        values.add(byId);
        values.add(byId1);

        List<User> allValues = userServiceImpl.getAllValues();

        assertEquals(values, allValues, "Списки идентичны");
    }

    @Test
    void create() {
        long id = 3;
        String name = "Maxim";
        String email = "maxim@gmail.com";
        String login = "maximBabyBon94";
        LocalDate localDate = LocalDate.of(1994, 2, 3);

        User userLocal = new User();
        userLocal.setName(name);
        userLocal.setEmail(email);
        userLocal.setLogin(login);
        userLocal.setBirthday(localDate);

        Optional<User> createdUser = Optional.ofNullable(userServiceImpl.create(userLocal));

        assertThat(createdUser).isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user).hasFieldOrPropertyWithValue("id", id);
                    assertThat(user).hasFieldOrPropertyWithValue("name", name);
                    assertThat(user).hasFieldOrPropertyWithValue("email", email);
                    assertThat(user).hasFieldOrPropertyWithValue("login", login);
                    assertThat(user).hasFieldOrPropertyWithValue("birthday", localDate);
                });
    }

    @Test
    void getById() {
        long id = 1;
        String name = "NastyaBaby";
        String email = "baby95@mail.ru";
        String login = "SweetLady";
        LocalDate localDate = LocalDate.of(1999, 9, 25);
        User userLocal = new User();

        userLocal.setId(id);
        userLocal.setName(name);
        userLocal.setEmail(email);
        userLocal.setLogin(login);
        userLocal.setBirthday(localDate);

        User byId = userServiceImpl.getById(id);

        assertEquals(userLocal, byId, "Пользователь соответсвует");
    }

    @Test
    void update() {
        long id = 1;
        long anotherId = 2;
        String name = "NewNastyaBaby";
        String email = "Newbaby95@mail.ru";
        String login = "NewSweetLady";
        LocalDate localDate = LocalDate.of(1999, 7, 25);
        User userLocal = new User();

        userLocal.setId(id);
        userLocal.setName(name);
        userLocal.setEmail(email);
        userLocal.setLogin(login);
        userLocal.setBirthday(localDate);

        User byId1 = userServiceImpl.getById(id);
        User byId2 = userServiceImpl.getById(anotherId);

        userServiceImpl.addFriend(userLocal.getId(), byId2.getId());
        userServiceImpl.confirmFriend(userLocal.getId(), byId2.getId());

        userServiceImpl.update(userLocal);

        User byId1Updated = userServiceImpl.getById(id);

        assertNotEquals(userLocal, byId1Updated, "Пользователь обновлён");
    }

    @Test
    void deleteById() {
        String name = "NastyaBaby";
        String email = "baby95@mail.ru";
        String login = "SweetLady";
        LocalDate localDate = LocalDate.of(1999, 9, 25);
        User userLocal = new User();

        userLocal.setName(name);
        userLocal.setEmail(email);
        userLocal.setLogin(login);
        userLocal.setBirthday(localDate);

        User user = userServiceImpl.create(userLocal);
        userServiceImpl.deleteById(user.getId());

        List<User> all = userServiceImpl.getAllValues();
        boolean contains = all.contains(user);

        assertFalse(contains, "Пользователя больше не существует");
    }

    @Test
    void addFriend() {
        long userId1 = 1;
        long userId2 = 2;
        User byId1 = userServiceImpl.getById(userId1);
        User byId2 = userServiceImpl.getById(userId2);

        Set<User> friendsLocal = new HashSet<>();
        friendsLocal.add(byId2);

        userServiceImpl.addFriend(byId1.getId(), byId2.getId());
        userServiceImpl.confirmFriend(byId1.getId(), byId2.getId());
        Set<User> friends = userServiceImpl.getFriends(byId1.getId());

        assertEquals(friendsLocal, friends, "Список друзей идентичен");
    }


    @Test
    void deleteFriend() {
        long friendSeq = 1;
        long userId1 = 1;
        long userId2 = 2;
        User byId1 = userServiceImpl.getById(userId1);
        User byId2 = userServiceImpl.getById(userId2);
        userServiceImpl.addFriend(byId1.getId(), byId2.getId());

        List<Friend> friendsLocal = new ArrayList<>();
        Friend friend = new Friend();
        friend.setId(friendSeq);
        friend.setUserId(byId1.getId());
        friend.setFriendId(byId2.getId());
        friend.setFriendship(Friendship.CONFIRMED);

        userServiceImpl.deleteFriendById(byId1.getId(), byId2.getId());
        Set<User> friends1 = userServiceImpl.getFriends(byId1.getId());

        assertNotEquals(friendsLocal, friends1, "В Списках друзей больше не содержатся");
    }

    @Test
    void confirmFriend() {
        long userId1 = 1;
        long userId2 = 2;
        User byId1 = userServiceImpl.getById(userId1);
        User byId2 = userServiceImpl.getById(userId2);
        userServiceImpl.addFriend(byId1.getId(), byId2.getId());

        Friend friend = new Friend();
        friend.setId(byId2.getId());
        friend.setUserId(byId1.getId());
        friend.setFriendId(byId2.getId());
        friend.setFriendship(Friendship.CONFIRMED);

        Friend confirmFriend = userServiceImpl.confirmFriend(byId1.getId(), byId2.getId());

        assertEquals(friend.getFriendship(), confirmFriend.getFriendship(), "Друзья подтверждены");
    }

    @Test
    void getFriends() {
        long userId1 = 1;
        long userId2 = 2;
        Set<User> localFriends = new HashSet<>();

        User byId1 = userServiceImpl.getById(userId1);
        User byId2 = userServiceImpl.getById(userId2);

        Set<User> friends = userServiceImpl.getFriends(byId1.getId());

        assertEquals(localFriends, friends, "Списки друзей пусты");

        userServiceImpl.addFriend(byId1.getId(), byId2.getId());
        userServiceImpl.confirmFriend(byId1.getId(), byId2.getId());
        localFriends.add(byId2);

        friends = userServiceImpl.getFriends(byId1.getId());

        assertEquals(localFriends, friends, "Теперь содержат в списках друга");
    }
}