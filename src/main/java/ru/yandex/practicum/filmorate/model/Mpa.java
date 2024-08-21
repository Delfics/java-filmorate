package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Mpa {
    @NotNull
    long id;
    String name;
}
