package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Like {
    @NotNull
    long id;
    long userId;
    long filmId;
}
