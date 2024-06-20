package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();


    @Override
    public Map<Long, Film> getAll() {
        return films;
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
    public void remove(Film film) {
        films.remove(film.getId());
    }

    @Override
    public long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
