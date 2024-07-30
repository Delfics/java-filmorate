package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.enums.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface FilmService {
    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    List<Film> getPopularFilms(Long size);

    Collection<Film> getAllValues();

    Film create(Film film);

    Film getById(Long id);

    Film update(Film newFilm);

    void deleteById(Long id);

    List<Mpa> getMpa();

    Mpa getMpaByFilmId(Long filmMpaId);

    Set<Genre> getGenresById(Long filmId);

    Set<Genre> getGenres();
}
