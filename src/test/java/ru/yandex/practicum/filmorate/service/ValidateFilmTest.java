package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ValidateFilmTest {
    Film film;


    @Test
    void shouldGetNameNull() {
        film = new Film();
        film.setDescription("About Film");
        film.setDuration(150);
        film.setReleaseDate(LocalDate.of(2000, 9, 19));

        ValidationException thrown = assertThrows(ValidationException.class,
                () -> ValidateFilm.validate(film), "Название не может быть пустым");

        assertTrue(thrown.getMessage().contains("Название не может быть пустым"));
    }

    @Test
    void shouldGetNameEmpty() {
        film = new Film();
        film.setName("");
        film.setDescription("About Film");
        film.setDuration(150);
        film.setReleaseDate(LocalDate.of(2000, 9, 19));

        ValidationException thrown = assertThrows(ValidationException.class,
                () -> ValidateFilm.validate(film), "Название не может быть пустым");

        assertTrue(thrown.getMessage().contains("Название не может быть пустым"));
    }

    @Test
    void shouldGetDescriptionMoreThan200() {
        film = new Film();
        film.setName("Film");
        film.setDescription("TestDescriptionTestDescriptionTestDescriptionTestDescriptionTestDescriptionTestDescript" +
                "nTestDescriptionTestDescriptionTestDescriptionTestDescriptionTestDescriptionTestDescriptionTestDesc" +
                "tionTestDescriptionT");
        film.setDuration(150);
        film.setReleaseDate(LocalDate.of(2000, 9, 19));

        ValidationException thrown = assertThrows(ValidationException.class,
                () -> ValidateFilm.validate(film), "Максимальная длина описания — 200 символов");

        assertTrue(thrown.getMessage().contains("Максимальная длина описания — 200 символов"));
    }

    @Test
    void shouldGetWrongData() {
        film = new Film();
        film.setName("Film");
        film.setDescription("About Film");
        film.setDuration(150);
        film.setReleaseDate(LocalDate.of(1893, 9, 19));

        ValidationException thrown = assertThrows(ValidationException.class,
                () -> ValidateFilm.validate(film), "Дата релиза — не раньше 28 декабря 1895 года");

        assertTrue(thrown.getMessage().contains("Дата релиза — не раньше 28 декабря 1895 года"));
    }

    @Test
    void shouldGetDurationNegativeNumber() {
        film = new Film();
        film.setName("Film");
        film.setDescription("About Film");
        film.setDuration(-150);
        film.setReleaseDate(LocalDate.of(1993, 9, 19));

        ValidationException thrown = assertThrows(ValidationException.class,
                () -> ValidateFilm.validate(film), "Продолжительность фильма должна быть положительным числом");

        assertTrue(thrown.getMessage().contains("Продолжительность фильма должна быть положительным числом"));
    }
}