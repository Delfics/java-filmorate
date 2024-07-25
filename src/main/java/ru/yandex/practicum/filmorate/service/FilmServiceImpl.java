package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dao.film.FilmStorage;
import ru.yandex.practicum.filmorate.dao.user.UserStorage;

import java.util.*;

@Service
public class FilmServiceImpl implements FilmService {
    private static final Logger log = LoggerFactory.getLogger(FilmServiceImpl.class);
    private final FilmStorage inMemoryFilmStorage;
    private final UserStorage inMemoryUserStorage;

    @Autowired
    public FilmServiceImpl(FilmStorage inMemoryFilmStorage, UserStorage inMemoryUserStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        log.info("Начало добавления лайка для фильма с id - {} от пользователя id - {}", filmId, userId);

        throwNotFoundIfUserOrUser1NotExist(filmId, userId);

        Film film = inMemoryFilmStorage.getAll().get(filmId);
        User user = inMemoryUserStorage.getAll().get(Math.toIntExact(userId));
        film.getLikes().add(user.getId());
        inMemoryFilmStorage.update(film);

        log.info("Лайк добавлен к фильму с id {} от пользователя с id: {}", film.getId(), user.getId());
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        log.info("Начало удаление лайка для фильма с id - {} от пользователя id - {}", filmId, userId);

        throwNotFoundIfUserOrUser1NotExist(filmId, userId);

        Film film = inMemoryFilmStorage.getAll().get(filmId);
        User user = inMemoryUserStorage.getAll().get(Math.toIntExact(userId));
        film.getLikes().remove(user.getId());
        inMemoryFilmStorage.update(film);

        log.info("Лайк удален у фильма с id {} от пользователя с id: {}", film.getId(), user.getId());
    }

    @Override
    public List<Film> getPopularFilms(Long size) {
        log.info("Начало получения списка фильмов, размер - {}", size);

        Comparator<Film> comparator = Comparator.comparing((o) -> o.getLikes().size());
        List<Film> filmsValue = new ArrayList<>(inMemoryFilmStorage.getAll().values());
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
    public Collection<Film> getAllValues() {
        return inMemoryFilmStorage.getAll().values();
    }

    @Override
    public Film getById(Long id) {
        if (inMemoryFilmStorage.getAll().containsKey(id)) {
            return inMemoryFilmStorage.getAll().get(id);
        } else {
            throw new NotFoundException("Фильм не найден с таким id " + id);
        }
    }

    @Override
    public Film create(Film film) {
        log.info("Начало создания film - {}", film.getName());

        ValidateFilm.validate(film);
        film.setId(inMemoryFilmStorage.getNextId());
        Film film1 = inMemoryFilmStorage.create(film);
        if (inMemoryFilmStorage.getAll().containsKey(film.getId())) {
            log.debug("Успешно создался film с id - {}", film.getId());
        }
        return film1;
    }

    @Override
    public Film update(Film newFilm) {
        log.info("Начало обновления film - {}", newFilm.getName());
        ValidateFilm.validate(newFilm);
        if (inMemoryFilmStorage.getAll().containsKey(newFilm.getId())) {
            Film update = inMemoryFilmStorage.update(newFilm);
            log.debug("Успешное обновление film - {}", newFilm.getName());
            return update;
        } else {
            log.error("Неправильный id");
            throw new NotFoundException("Не содержит данный id");
        }
    }

    @Override
    public void remove(Film film) {
        log.info("Начало удаление фильма - {}", film);

        if (inMemoryFilmStorage.getAll().containsKey(film.getId())) {
            inMemoryFilmStorage.remove(film);

            log.debug("Успешное удаление фильма");
        } else {
            throw new NotFoundException("Не содержит фильм с таким " + film.getId());
        }
    }

    private void throwNotFoundIfUserOrUser1NotExist(Long filmId, Long userId) {
        if (!inMemoryUserStorage.getAll().contains(userId)) {
            throw new NotFoundException("Пользователь с таким id: " + userId + " не найден");
        } else if (!inMemoryFilmStorage.getAll().containsKey(filmId)) {
            throw new NotFoundException("Фильм с таким id: " + filmId + " не найден");
        }
    }
}
