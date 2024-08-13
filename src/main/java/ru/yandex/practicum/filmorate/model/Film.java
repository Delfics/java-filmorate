package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class Film {
    Long id;
    @NotNull
    String name;
    String description;
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate releaseDate;
    @NotNull
    Integer duration;
    Set<Like> likes = new HashSet<>();
    List<Genre> genres = new ArrayList<>();
    Mpa mpa;
}
