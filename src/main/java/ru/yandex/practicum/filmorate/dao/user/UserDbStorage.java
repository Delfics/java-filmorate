package ru.yandex.practicum.filmorate.dao.user;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FriendRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.Friendship;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;
    private final FriendRowMapper friendRowMapper;
    private GeneratedKeyHolder keyHolder;

    @Override
    public List<User> getAll() {
        String query = "SELECT * FROM users;";
        return jdbcTemplate.query(query, userRowMapper);
    }

    @Override
    public User create(User user) {
        keyHolder = new GeneratedKeyHolder();

        int result = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement
                    ("INSERT INTO users (id, email, login, name, birthday)" +
                                    " VALUES (NEXT VALUE FOR USER_SEQ, ?, ?, ? ,?,);",
                            Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, user.getEmail());
            ps.setObject(2, user.getLogin());
            ps.setObject(3, user.getName());
            ps.setObject(4, user.getBirthday());

            return ps;
        }, keyHolder);

        Integer generatedId = keyHolder.getKeyAs(Integer.class);

        user.setId((long) generatedId);

        return user;
    }

    @Override
    public User getById(Long id) {
        String query = "SELECT * FROM users WHERE id = ?;";
        return jdbcTemplate.queryForObject(query, userRowMapper, id);
    }

    @Override
    public User update(User newUser) {
        keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement
                    ("UPDATE INTO users (id, email, login, name, birthday)" +
                                    " VALUES (?, ?, ?, ? ,?) WHERE id = ? ;",
                            Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, newUser.getId());
            ps.setObject(2, newUser.getEmail());
            ps.setObject(3, newUser.getLogin());
            ps.setObject(4, newUser.getName());
            ps.setObject(5, newUser.getBirthday());
            ps.setLong(6, newUser.getId());

            return ps;
        }, keyHolder);


        Integer generatedId = keyHolder.getKeyAs(Integer.class);

        newUser.setId((long) generatedId);
        return newUser;
    }

    @Override
    public boolean deleteById(Long id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id = ?", id) > 0;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO friends (user_id, friend_id, friendship) VALUES (?, ?, ?);");
            ps.setObject(1, userId);
            ps.setObject(2, friendId);
            ps.setObject(3, Friendship.NOT_CONFIRMED);
            return ps;
        }, keyHolder);
    }

    @Override
    public boolean deleteFriend(Long userId, Long friendId) {
        return jdbcTemplate.update("DELETE FROM friends WHERE user_id = ? " +
                "AND friends_id = ?;", userId, friendId) > 0;
    }

    @Override
    public void confirmFriend(Long userId, Long friendId) {

        String query = "SELECT id FROM friedns WHERE user_id = ? AND friend_id = ?;";
        int id = jdbcTemplate.queryForObject(query, friendRowMapper, userId, friendId);

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement
                    ("UPDATE INTO friends (id, user_id, friend_id, friendship)" +
                                    " VALUES (?, ?, ?, ?);",
                            Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, id);
            ps.setObject(2, userId);
            ps.setObject(3, friendId);
            ps.setObject(4, Friendship.CONFIRMED);
            return ps;
        });
    }
}