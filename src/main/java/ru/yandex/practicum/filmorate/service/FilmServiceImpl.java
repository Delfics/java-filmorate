package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.user.UserStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.dao.film.FilmStorage;

import java.util.*;

@Service
public class FilmServiceImpl implements FilmService {
    private static final Logger log = LoggerFactory.getLogger(FilmServiceImpl.class);
    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmServiceImpl(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    @Override
    public Set<Like> getLikes(Long filmId) {
        return filmStorage.getLikes(filmId);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        log.info("Начало добавления лайка для фильма с id - {} от пользователя id - {}", filmId, userId);

        throwNotFoundIfFilmOrUserNotExist(filmId, userId);

        Film film = filmStorage.getById(filmId);
        User user = userService.getById(userId);
        if (film != null && user != null) {
            filmStorage.addLike(film.getId(), user.getId());
        } else {
            throwNotFoundIfFilmOrUserNotExist(film.getId(), user.getId());
        }
        log.info("Лайк добавлен к фильму с id {} от пользователя с id: {}", film.getId(), user.getId());
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        log.info("Начало удаление лайка для фильма с id - {} от пользователя id - {}", filmId, userId);

        throwNotFoundIfFilmOrUserNotExist(filmId, userId);

        Film film = filmStorage.getById(filmId);
        User user = userService.getById(userId);
        if (film != null && user != null) {
            boolean result = filmStorage.deleteLike(film.getId(), user.getId());
            if (result) {
                log.info("Лайк удален у фильма с id {} от пользователя с id: {}", film.getId(), user.getId());
            }
        } else {
            throwNotFoundIfFilmOrUserNotExist(film.getId(), user.getId());
        }
    }

    @Override
    public List<Film> getPopularFilms(Long size) {
        log.info("Начало получения списка фильмов, размер - {}", size);

        Comparator<Film> comparator = Comparator.comparing((o) -> o.getLikes().size());
        List<Film> filmsValue = new ArrayList<>(filmStorage.getAll());
        if (filmsValue.isEmpty()) {
            throw new NotFoundException("Список фильмов пуст");
        }
        filmsValue.sort(comparator);
        List<Film> popFilms = new ArrayList<>();

        if (filmsValue.size() > size) {
            long getSize = filmsValue.size() - size;
            for (int i = (int) getSize; i < filmsValue.size(); i++) {
                popFilms.add(filmsValue.get(i));
            }
        } else {
            popFilms = filmsValue;

        }
        popFilms.sort(comparator);
        return popFilms.reversed();
    }

    @Override
    public List<Film> getAllValues() {
        List<Film> all = filmStorage.getAll();
        for (Film film : all) {
            if (film.getMpaId() != 0) {
                Mpa mpa = getMpaByFilmId(film.getId());
                film.setMpa(mpa);
            }
            film.setGenres(getGenresByFilmId(film.getId()));
            if (film.getLikes() != null) {
                film.setLikes(filmStorage.getLikes(film.getId()));
            }
        }
        return all;
    }

    @Override
    public Film getById(Long id) {
        try {
            return filmStorage.getById(id);
        } catch (NotFoundException e) {
            throw new NotFoundException("Фильм не найден с таким id " + id);
        }
    }


    @Override
    public Film create(Film film) {
        log.info("Начало создания film - {}", film.getName());

        ValidateFilm.validate(film);

        Film film1 = filmStorage.create(film);
        if (filmStorage.getAll().contains(film)) {
            log.debug("Успешно создался film с id - {}", film.getId());
        }
        return film1;
    }

    @Override
    public Film update(Film newFilm) {
        log.info("Начало обновления film - {}", newFilm.getName());
        ValidateFilm.validate(newFilm);
        if (filmStorage.getById(newFilm.getId()).getId().equals(newFilm.getId())) {
            Film update = filmStorage.update(newFilm);
            log.debug("Успешное обновление film - {}", newFilm.getName());
            return update;
        } else {
            log.error("Неправильный id");
            throw new NotFoundException("Не содержит данный id");
        }
    }

    @Override
    public void deleteById(Long filmId) {
        log.info("Начало удаление фильма - {}", filmId);

        if (filmStorage.getById(filmId).getId().equals(filmId)) {
            filmStorage.deleteById(filmId);

            log.debug("Успешное удаление фильма");
        } else {
            throw new NotFoundException("Не содержит фильм с таким " + filmId);
        }
    }

    @Override
    public List<Mpa> getMpa() {
        return filmStorage.getMpa();
    }

    @Override
    public Mpa getMpaById(Long mpaId) {
        return filmStorage.getMpaById(mpaId);
    }

    @Override
    public Mpa getMpaByFilmId(Long filmMpaId) {
        return filmStorage.getMpaByFilmId(filmMpaId);
    }

    @Override
    public List<Genre> getGenres() {
        return filmStorage.getGenres();
    }

    @Override
    public Genre getGenreById(Long genreId) {
        return filmStorage.getGenreById(genreId);
    }

    @Override
    public Set<Genre> getGenresByFilmId(Long filmId) {
        return filmStorage.getGenresByFilmId(filmId);
    }

    private void throwNotFoundIfFilmOrUserNotExist(Long filmId, Long userId) {
        if (!userService.getAllValues().contains(userService.getById(userId))) {
            throw new NotFoundException("Пользователь с таким id: " + userId + " не найден");
        } else if (!getAllValues().contains(filmStorage.getById(filmId))) {
            throw new NotFoundException("Фильм с таким id: " + filmId + " не найден");
        }
    }
}
