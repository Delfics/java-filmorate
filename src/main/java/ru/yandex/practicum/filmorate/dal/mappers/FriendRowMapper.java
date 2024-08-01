package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.enums.Friendship;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FriendRowMapper implements RowMapper<Friend> {

    @Override
    public Friend mapRow(ResultSet rs, int rowNum) throws SQLException {
        Friend friend = new Friend();
        friend.setId(rs.getInt("id"));
        friend.setUserId(rs.getInt("user_id"));
        friend.setFriendId(rs.getInt("friend_id"));
        friend.setFriendship(Friendship.valueOf(rs.getString("friendship")));
        return friend;
    }
}
