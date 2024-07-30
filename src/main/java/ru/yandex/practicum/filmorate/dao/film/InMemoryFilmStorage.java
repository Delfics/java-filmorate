package ru.yandex.practicum.filmorate.dao.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.enums.Genre;

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
        return List.of();
    }

    @Override
    public Mpa getMpaByFilmId(Long filmMpaId) {
        return null;
    }

    @Override
    public Set<Genre> getGenresById(Long filmId) {
        return Set.of();
    }

    @Override
    public Set<Genre> getGenres() {
        return Set.of();
    }


    @Override
    public Set<Long> getLikes(Long filmId) {
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
