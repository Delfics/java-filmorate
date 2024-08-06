package ru.yandex.practicum.filmorate.dao.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Set;

public interface FilmStorage {
    List<Film> getAll();

    Film create(Film film);

    Film update(Film newFilm);

    Film getById(Long id);

    boolean deleteById(Long id);

    List<Mpa> getMpa();

    Mpa getMpaById(Long MpaId);

    Mpa getMpaByFilmId(Long filmMpaId);

    Genre getGenreById(Long genreId);

    List<Genre> getGenres();

    Set<Genre> getGenresByFilmId(Long filmId);

    Set<Like> getLikes(Long filmId);

    void addLike(Long filmId, Long userId);

    boolean deleteLike(Long filmId, Long userId);
}
