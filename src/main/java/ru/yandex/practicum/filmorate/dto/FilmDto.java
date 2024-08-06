package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class FilmDto {
    Long id;
    @NotNull
    String name;
    String description;
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate releaseDate;
    @NotNull
    Integer duration;
    Set<Like> likes = new HashSet<>();
    Set<Genre> genres = new HashSet<>();
    long mpaId;
    Mpa mpa;
}
