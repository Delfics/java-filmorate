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

    boolean deleteById(Long id);

    List<Mpa> getMpa();

    Mpa getMpaByFilmId(Long filmMpaId);

    Set<Genre> getGenresById(Long filmId);

    Set<Genre> getGenres();

    Set<Long> getLikes(Long filmId);

    void addLike(Long filmId, Long userId);

    boolean deleteLike(Long filmId, Long userId);
}
