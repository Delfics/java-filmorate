package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    Long id;
    @Email
    @NotNull
    String email;
    @NotNull
    @NotBlank
    String login;
    String name;
    LocalDate birthday;
}