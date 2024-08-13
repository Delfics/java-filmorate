package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class UserResultSetMapper implements ResultSetExtractor<List<User>> {

    @Override
    public List<User> extractData(ResultSet rs) throws SQLException {
        List<User> users = new ArrayList<>();
        Set<Long> friends = new HashSet<>();

        User currentUser = null;

        while (rs.next()) {
            long userId = rs.getLong("users.id");

            if (currentUser == null || currentUser.getId() != userId) {
                if (currentUser != null) {
                    currentUser.setFriends(friends);
                    users.add(currentUser);
                }

                currentUser = createUser(rs);
                friends = new HashSet<>();
            }

            long friendId = rs.getLong("friends.friend_id");
            if (friendId != 0 && friendId != currentUser.getId()) {
                friends.add(friendId);
            }
        }

        if (currentUser != null) {
            currentUser.setFriends(friends);
            users.add(currentUser);
        }

        return users;
    }

    private User createUser(ResultSet rs) throws SQLException {
        User user = new User();

        user.setId(rs.getLong("id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        String birthday = rs.getString("birthday");
        user.setBirthday(LocalDate.parse(birthday));
        return user;
    }

}
