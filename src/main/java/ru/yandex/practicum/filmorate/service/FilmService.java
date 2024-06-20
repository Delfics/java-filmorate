package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmService {
    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    List<Film> getPopularFilms(Long size);

    Collection<Film> getAllValues();

    Film create(Film film);

    Film getById(Long id);

    Film update(Film newFilm);

    void remove(Film film);
}
