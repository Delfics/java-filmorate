/*
package ru.yandex.practicum.filmorate.dao.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();


    public Map<Long, Film> getAllFilm() {
        return films;
    }

    @Override
    public List<Film> getAll() {
        return List.of();
    }

    @Override
    public Film create(Film film) {
        films.put(film.getId(), film);
        return films.get(film.getId());
    }

    @Override
    public Film update(Film newFilm) {
        films.put(newFilm.getId(), newFilm);
        return films.get(newFilm.getId());
    }

    @Override
    public Film getById(Long id) {
        return films.get(id);
    }

    @Override
    public boolean deleteById(Long id) {
        return false;
    }

    @Override
    public List<Mpa> getMpa() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Mpa getMpaById(Long MpaId) {
        return null;
    }

    @Override
    public Mpa getMpaByFilmId(Long filmMpaId) {
        return null;
    }

    @Override
    public Genre getGenreById(Long filmId) {
        return null;
    }

    @Override
    public List<Genre> getGenres() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<Genre> getGenresByFilmId(Long filmId) {
        return Set.of();
    }


    @Override
    public Set<Like> getLikes(Long filmId) {
        return Set.of();
    }

    @Override
    public void addLike(Long filmId, Long userId) {

    }

    @Override
    public boolean deleteLike(Long filmId, Long userId) {
        return false;
    }


    public void remove(Film film) {
        films.remove(film.getId());
    }


    public long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
*/
