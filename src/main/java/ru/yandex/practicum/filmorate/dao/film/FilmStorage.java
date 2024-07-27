package ru.yandex.practicum.filmorate.dao.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.enums.Genre;

import java.util.List;
import java.util.Set;

public interface FilmStorage {
    List<Film> getAll();

    Film create(Film film);

    Film update(Film newFilm);

    Film getById(Long id);

    boolean deleteById (Long id);

    Mpa getMpa(Long filmMpaId);

    Set<Genre> getGenres(Long filmId);

    Set<Long> getLikes (Long filmId);
}
