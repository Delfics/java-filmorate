package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmServiceImpl;

    public FilmController(FilmService filmServiceImpl) {
        this.filmServiceImpl = filmServiceImpl;
    }

    @GetMapping
    public Collection<Film> getAll() {
        return filmServiceImpl.getAllValues();
    }

    @GetMapping("/{id}")
    public Film getById(@PathVariable Long id) {
        return filmServiceImpl.getById(id);
    }

    @GetMapping("/popular{count}")
    public List<Film> getPopularFilms(@PathVariable @RequestParam(defaultValue = "10") Long count) {
        return filmServiceImpl.getPopularFilms(count);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmServiceImpl.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        return filmServiceImpl.update(newFilm);
    }

    @PutMapping("{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        filmServiceImpl.addLike(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        filmServiceImpl.removeLike(id, userId);
    }

}
