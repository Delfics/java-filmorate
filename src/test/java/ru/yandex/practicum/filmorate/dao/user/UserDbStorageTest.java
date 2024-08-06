package ru.yandex.practicum.filmorate.dao.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.practicum.filmorate.dal.mappers.FriendRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.Friendship;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@JdbcTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase
@Import({UserDbStorage.class, UserRowMapper.class, FriendRowMapper.class})
class UserDbStorageTest {
    @Autowired
    private UserStorage userDbStorage;

    @Test
    void getAll() {
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

        Optional<User> createdUser = Optional.ofNullable(userDbStorage.create(userLocal));

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

        User byId = userDbStorage.getById(id);

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

        User byId1 = userDbStorage.getById(id);
        User byId2 = userDbStorage.getById(anotherId);

        userDbStorage.addFriend(userLocal.getId(), byId2.getId());
        userDbStorage.confirmFriend(userLocal.getId(), byId2.getId());

        byId1 = userDbStorage.getById(id);

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

        User user = userDbStorage.create(userLocal);
        userDbStorage.deleteById(user.getId());

        List<User> all = userDbStorage.getAll();
        boolean contains = all.contains(user);

        assertFalse(contains, "Пользователя больше не существует");
    }

    @Test
    void addFriend() {
        long friendSeq = 2;
        long userId1 = 1;
        long userId2 = 2;
        User byId1 = userDbStorage.getById(userId1);
        User byId2 = userDbStorage.getById(userId2);

        List<Friend> friendsLocal = new ArrayList<>();
        Friend friend = new Friend();
        friend.setId(friendSeq);
        friend.setUserId(byId1.getId());
        friend.setFriendId(byId2.getId());
        friend.setFriendship(Friendship.CONFIRMED);

        friendsLocal.add(friend);

        userDbStorage.addFriend(byId1.getId(), byId2.getId());
        userDbStorage.confirmFriend(byId1.getId(), byId2.getId());
        List<Friend> friends = userDbStorage.getFriends(byId1.getId());

        assertEquals(friendsLocal, friends, "Список друзей идентичен");
    }


    @Test
    void deleteFriend() {
        long friendSeq = 1;
        long userId1 = 1;
        long userId2 = 2;
        User byId1 = userDbStorage.getById(userId1);
        User byId2 = userDbStorage.getById(userId2);
        userDbStorage.addFriend(byId1.getId(), byId2.getId());

        List<Friend> friendsLocal = new ArrayList<>();
        Friend friend = new Friend();
        friend.setId(friendSeq);
        friend.setUserId(byId1.getId());
        friend.setFriendId(byId2.getId());
        friend.setFriendship(Friendship.CONFIRMED);

        friendsLocal.add(friend);

        List<Friend> friends = userDbStorage.getFriends(byId1.getId());

        userDbStorage.deleteFriend(byId1.getId(), byId2.getId());
        List<Friend> friends1 = userDbStorage.getFriends(byId1.getId());

        assertNotEquals(friendsLocal, friends1, "В Списках друзей больше не содержатся" );
    }

    @Test
    void confirmFriend() {
        long userId1 = 1;
        long userId2 = 2;
        User byId1 = userDbStorage.getById(userId1);
        User byId2 = userDbStorage.getById(userId2);
        userDbStorage.addFriend(byId1.getId(), byId2.getId());

        Friend friend = new Friend();
        friend.setUserId(byId1.getId());
        friend.setFriendId(byId2.getId());
        friend.setFriendship(Friendship.NOT_CONFIRMED);

        List<Friend> friends = userDbStorage.getFriends(byId1.getId());
        int friendId = 0;
        Friend friend1 = friends.get(friendId);

        assertEquals(friend.getFriendship(), friend1.getFriendship(), "Друзья не подтверждены" );

        friend.setFriendship(Friendship.CONFIRMED);

        userDbStorage.confirmFriend(byId1.getId(), byId2.getId());

        List<Friend> friendsConfirmed = userDbStorage.getFriends(byId1.getId());
        Friend friend2 = friendsConfirmed.get(friendId);

        assertEquals(friend.getFriendship(), friend2.getFriendship(), "Друзья подтверждены");
    }

    @Test
    void getFriends() {
    }
}