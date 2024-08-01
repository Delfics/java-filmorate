package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.enums.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Set;

@RestController
@RequestMapping("/genres")
public class GenresController {
    private final FilmService filmService;
    public GenresController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Set<Genre> getGenres() {
        return filmService.getGenres();
    }

    @GetMapping("/{id}")
    public Set<Genre> getGenresById(@PathVariable Long id) {
        return filmService.getGenresById(id);
    }
}
