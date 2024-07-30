package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.mappers.MapFilmToFilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.enums.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<FilmDto> getAll() {
        Collection<Film> allValues = filmService.getAllValues();
        return allValues.stream()
                .map(MapFilmToFilmDto::filmToFilmDto)
                .toList();
    }

    @GetMapping("/{id}")
    public FilmDto getById(@PathVariable Long id) {
        Film byId = filmService.getById(id);
        return MapFilmToFilmDto.filmToFilmDto(byId);
    }

    @GetMapping("/popular{count}")
    public List<FilmDto> getPopularFilms(@PathVariable @RequestParam(defaultValue = "10") Long count) {
        List<Film> popularFilms = filmService.getPopularFilms(count);
       return popularFilms.stream()
                .map(MapFilmToFilmDto::filmToFilmDto)
                .toList();
    }

    @GetMapping("/genres")
    public Set<Genre> getGenres() {
        return filmService.getGenres();
    }

    @GetMapping("/genres/{id}")
    public Set<Genre> getGenresById(@PathVariable Long id) {
        return filmService.getGenresById(id);
    }

    @GetMapping("/mpa")
    public List<Mpa> getMpa() {
        return filmService.getMpa();
    }

    @GetMapping("/mpa/{id}")
    public Mpa getMpaById(@PathVariable Long id) {
        return filmService.getMpaByFilmId(id);
    }

    @PostMapping
    public FilmDto create(@Valid @RequestBody Film film) {
        Film film1 = filmService.create(film);
       return MapFilmToFilmDto.filmToFilmDto(film1);
    }

    @PutMapping
    public FilmDto update(@Valid @RequestBody Film newFilm) {
        Film update = filmService.update(newFilm);
       return MapFilmToFilmDto.filmToFilmDto(update);
    }

    @PutMapping("{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.deleteLike(id, userId);
    }

}
