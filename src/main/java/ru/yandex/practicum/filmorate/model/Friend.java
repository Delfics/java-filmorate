package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.Friendship;

@Data
public class Friend {
    long id;
    long userId;
    long friendId;
    Friendship friendship;

}
