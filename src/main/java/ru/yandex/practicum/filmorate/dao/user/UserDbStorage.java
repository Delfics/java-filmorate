package ru.yandex.practicum.filmorate.dao.user;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FriendRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.UserResultSetMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.Friendship;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserResultSetMapper userResultSetMapper;
    private final FriendRowMapper friendRowMapper;
    private GeneratedKeyHolder keyHolder;

    @Override
    public List<User> getAll() {
        String query = "SELECT DISTINCT users.id, users.email, users.login, users.name, users.birthday, " +
                "friends.friend_id " +
                "FROM users " +
                "LEFT JOIN friends ON users.id = friends.user_id ";
        return jdbcTemplate.query(query, userResultSetMapper);
    }

    @Override
    public User create(User user) {
        keyHolder = new GeneratedKeyHolder();

        int result = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO " +
                            "users (id, email, login, name, birthday)" +
                            " VALUES (NEXT VALUE FOR USER_SEQ, ?, ?, ? ,?);",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, user.getEmail());
            ps.setObject(2, user.getLogin());
            ps.setObject(3, user.getName());
            ps.setObject(4, user.getBirthday());

            return ps;
        }, keyHolder);

        if (result < 1) {
            throw new NotFoundException("Пользователь " + user.getName() + " не создан");
        }

        Integer generatedId = keyHolder.getKeyAs(Integer.class);

        user.setId((long) generatedId);

        return user;
    }

    @Override
    public User getById(Long id) {
        try {
            String query = "SELECT DISTINCT users.id, users.email, users.login, users.name, users.birthday, " +
                    "friends.friend_id " +
                    "FROM users " +
                    "LEFT JOIN friends ON users.id = friends.user_id " +
                    "WHERE users.id = ?;";
            List<User> users = jdbcTemplate.query(query, userResultSetMapper, id);
            return users.get(0);
        } catch (RuntimeException e) {
            throw new NotFoundException("Пользователь с " + id + " не найден");
        }
    }

    @Override
    public User update(User newUser) {
        keyHolder = new GeneratedKeyHolder();

        int result = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("UPDATE users" +
                            " SET id = ?, email = ?, login = ?, name = ?, birthday = ?" +
                            "WHERE id = ? ;",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, newUser.getId());
            ps.setObject(2, newUser.getEmail());
            ps.setObject(3, newUser.getLogin());
            ps.setObject(4, newUser.getName());
            ps.setObject(5, newUser.getBirthday());
            ps.setLong(6, newUser.getId());

            return ps;
        }, keyHolder);

        if (result < 1) {
            throw new NotFoundException("Пользователь " + newUser.getName() + " не обновлен");
        }

        Integer generatedId = keyHolder.getKeyAs(Integer.class);

        newUser.setId((long) generatedId);
        return newUser;
    }

    @Override
    public boolean deleteById(Long id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id = ?", id) > 0;
    }

    @Override
    public User addFriend(Long userId, Long friendId) {
        keyHolder = new GeneratedKeyHolder();
        try {
            int result = jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO friends (user_id, friend_id, friendship) VALUES (?, ?, ?);");
                ps.setObject(1, userId);
                ps.setObject(2, friendId);
                ps.setObject(3, Friendship.NOT_CONFIRMED.getValue());
                return ps;
            }, keyHolder);

            if (result < 1) {
                throw new NotFoundException("Пользователь с " + userId + " или с " + friendId + " не обновлен");
            }
        } catch (RuntimeException e) {
            throw new NotFoundException("Добавление не существующих пользователей " + userId + " или " + friendId);
        }
        return getById(userId);
    }

    @Override
    public boolean deleteFriend(Long userId, Long friendId) {
        try {
            boolean userExist = userExists(userId);
            boolean friendExists = userExists(friendId);
            if (userExist && friendExists) {
                return jdbcTemplate.update("DELETE FROM friends WHERE user_id = ? AND friend_id = ? ;"
                        , userId, friendId) > 0;
            } else {
                throw new NotFoundException("Не содержит данного пользователя " + userId + " или " + friendId);
            }
        } catch (RuntimeException e) {
            throw new NotFoundException("Пользователи " + userId + " или " + friendId + " не найдены");
        }
    }

    @Override
    public Friend confirmFriend(Long userId, Long friendId) {
        try {

            String query = "SELECT * FROM friends WHERE user_id = ? AND friend_id = ?;";
            Friend friend = jdbcTemplate.queryForObject(query, friendRowMapper, userId, friendId);

            int result = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement("UPDATE friends SET friendship = ? WHERE id = ?;");
                ps.setObject(1, Friendship.CONFIRMED.getValue());
                ps.setObject(2, friend.getId());
                return ps;
            });
            if (result < 1) {
                throw new NotFoundException("Пользователь " + friendId + " не стал другом " + userId);
            }
            return jdbcTemplate.queryForObject(query, friendRowMapper, userId, friendId);

        } catch (RuntimeException e) {
            throw new NotFoundException("Пользователь " + friendId + " не найден");
        }
    }

    @Override
    public Set<Friend> getFriends(Long userId) {
        try {
            String query = "SELECT * FROM friends WHERE user_id = ?;";
            List<Friend> query1 = jdbcTemplate.query(query, friendRowMapper, userId);
            return new HashSet<>(query1);
        } catch (RuntimeException e) {
            throw new NotFoundException("Пользователь не найден с таким id " + userId);
        }
    }

    public boolean userExists(Long userId) {
        String sql = "SELECT EXISTS (SELECT 1 FROM users WHERE id = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, userId);
    }

    public Set<User> getFriends1(Long userId) {
        try {
            userExists(userId);
            String query = "SELECT DISTINCT users.id, users.email, users.login, users.name, " +
                    "users.birthday, friends.friend_id " +
                    "FROM users " +
                    "LEFT JOIN friends ON users.id = friends.user_id " +
                    "WHERE friends.user_id = ?";
            List<User> friends = jdbcTemplate.query(query, userResultSetMapper, userId);
            return new HashSet<>(friends);
        } catch (RuntimeException e) {
            throw new NotFoundException("Не найдены друзья у пользователя " + userId);
        }
    }
}
