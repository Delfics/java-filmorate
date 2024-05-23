package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.ValidateFilm;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final ValidateFilm validateFilm = new ValidateFilm(films, log);

    @GetMapping
    public Collection<Film> getAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Начало создания film - {}", film.getName());
        validateFilm.validate(film);
        film.setId(validateFilm.getNextId());
        films.put(film.getId(), film);
        log.info("Успешно создался film - {}", film.getName());
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        log.info("Начало обновления film - {}", newFilm.getName());
        validateFilm.validate(newFilm);
        if (films.containsKey(newFilm.getId())) {
            films.put(newFilm.getId(), newFilm);
            log.info("Успешное обновление film - {}", newFilm.getName());
        } else {
            log.error("Неправильный id");
            throw new RuntimeException("Не содержит данный id");
        }
        return newFilm;
    }
}
