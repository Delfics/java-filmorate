package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.model.enums.Genres;

@Data
@RequiredArgsConstructor
public class Genre {
    long id;
    Genres name;

    public Genre(long id, Genres name) {
        this.id = id;
        this.name = name;
    }
}
