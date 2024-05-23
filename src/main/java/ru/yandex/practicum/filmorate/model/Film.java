package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * Film.
 */
@Data
public class Film {
    Long id;
    @NotNull
    String name;
    String description;
    LocalDate releaseDate;
    @NotNull
    Integer duration;
}
