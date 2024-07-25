package ru.yandex.practicum.filmorate.dao.film;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;


@Repository
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    @Override
    public Map<Long, Film> getAll() {
        return Map.of();
    }

    @Override
    public Film create(Film film) {
        return null;
    }

    @Override
    public Film update(Film newFilm) {
        return null;
    }

    @Override
    public Film getById(Long id) {
        return null;
    }

    @Override
    public void remove(Film film) {

    }

    @Override
    public long getNextId() {
        return 0;
    }
}
