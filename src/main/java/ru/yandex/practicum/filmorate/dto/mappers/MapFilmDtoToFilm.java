package ru.yandex.practicum.filmorate.dto.mappers;

import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;

public class MapFilmDtoToFilm {
    public static Film filmDtoToFilm(FilmDto filmDto) {
        Film film = new Film();
        film.setId(filmDto.getId());
        film.setName(filmDto.getName());
        film.setDescription(filmDto.getDescription());
        film.setReleaseDate(filmDto.getReleaseDate());
        film.setDuration(filmDto.getDuration());
        film.setLikes(filmDto.getLikes());
        film.setGenres(filmDto.getGenres());
        film.setMpaId(filmDto.getMpaId());
        film.setMpa(filmDto.getMpa());
        return film;
    }
}
