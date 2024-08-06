package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.Genres;

@Data
public class Genre {
    long id;
    Genres name;
}
