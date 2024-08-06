package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.mappers.MapFilmDtoToFilm;
import ru.yandex.practicum.filmorate.dto.mappers.MapFilmToFilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<FilmDto> getAll() {
        List<Film> allValues = filmService.getAllValues();
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

    @PostMapping
    public FilmDto create(@Valid @RequestBody FilmDto filmDto) {
        Film film = MapFilmDtoToFilm.filmDtoToFilm(filmDto);
        Film film1 = filmService.create(film);
        return MapFilmToFilmDto.filmToFilmDto(film1);
    }

    @PutMapping
    public FilmDto update(@Valid @RequestBody FilmDto newFilmDto) {
        Film film = MapFilmDtoToFilm.filmDtoToFilm(newFilmDto);
        Film update = filmService.update(film);
        return MapFilmToFilmDto.filmToFilmDto(update);
    }

    @PutMapping("{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("{id}")
    public void deleteFilmById(@PathVariable Long id) {
        filmService.deleteById(id);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.deleteLike(id, userId);
    }

}
