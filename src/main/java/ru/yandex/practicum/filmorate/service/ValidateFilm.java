package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class ValidateFilm {
    private static final Logger log = LoggerFactory.getLogger(ValidateFilm.class);


    public static void validate(Film film) {
        int maxLength = 200;
        int zero = 0;
        LocalDate data = LocalDate.of(1895, 12, 28);
        if (film.getName() == null || film.getName().isEmpty()) {
            log.error("Имя фильма равно нулю");
            throw new ValidationException("Название не может быть пустым");
        } else if (film.getDescription().length() > maxLength) {
            log.error("Длина описания больше допустимой");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        } else if (film.getReleaseDate().isBefore(data)) {
            log.error("Выбрана неправильная дата");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        } else if (film.getDuration() < zero) {
            log.error("Неправильная продолжительность фильма");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }
}
