package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Like {
    @NotNull
    long id;
    long userId;
    long filmId;

    public Like() {
    }

    public Like(long id, long userId, long filmId) {
        this.id = id;
        this.userId = userId;
        this.filmId = filmId;
    }
}
