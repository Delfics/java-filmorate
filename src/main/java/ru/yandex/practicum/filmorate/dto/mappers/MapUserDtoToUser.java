package ru.yandex.practicum.filmorate.dto.mappers;

import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;

public class MapUserDtoToUser {
    public static User userDtoToUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setLogin(userDto.getLogin());
        user.setBirthday(userDto.getBirthday());
        user.setFriends(userDto.getFriends());
        return user;
    }
}
