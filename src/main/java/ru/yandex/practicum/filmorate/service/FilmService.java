package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Set;

public interface FilmService {
    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    Set<Like> getLikes(Long filmId);

    List<Film> getPopularFilms(Long size);

    List<Film> getAllValues();

    Film create(Film film);

    Film getById(Long id);

    Film update(Film newFilm);

    void deleteById(Long id);

    List<Mpa> getMpa();

    Mpa getMpaById(Long mpaId);

    Mpa getMpaByFilmId(Long filmMpaId);

    List<Genre> getGenresByFilmId(Long filmId);

    List<Genre> getGenres();

    Genre getGenreById(Long genreId);
}
