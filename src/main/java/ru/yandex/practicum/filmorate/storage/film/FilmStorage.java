package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;

public interface FilmStorage {
    Map<Long, Film> getAll();

    Film create(Film film);

    Film update(Film newFilm);

    Film getById(Long id);

    void remove(Film film);
}
