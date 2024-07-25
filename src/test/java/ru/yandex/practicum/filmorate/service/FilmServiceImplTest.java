package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dao.film.FilmStorage;
import ru.yandex.practicum.filmorate.dao.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.dao.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.dao.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilmServiceImplTest {
    private final FilmStorage inMemoryFilmStorage = new InMemoryFilmStorage();
    private final UserStorage inMemoryUserStorage = new InMemoryUserStorage();
    private final UserService userServiceImpl = new UserServiceImpl(inMemoryUserStorage);
    private final FilmService filmServiceImpl = new FilmServiceImpl(inMemoryFilmStorage, inMemoryUserStorage);

    @Test
    void shouldCreate() {
        Film film = new Film();
        film.setName("TestFilm");
        film.setDuration(90);
        film.setReleaseDate(LocalDate.of(2000, 5, 25));
        film.setDescription("About Test Film");

        filmServiceImpl.create(film);

        assertEquals(film, inMemoryFilmStorage.getAll().get(film.getId()), "Создали фильм");
    }

    @Test
    void shouldRemove() {
        Film film = new Film();
        film.setName("TestFilm");
        film.setDuration(90);
        film.setReleaseDate(LocalDate.of(2000, 5, 25));
        film.setDescription("About Test Film");

        filmServiceImpl.create(film);
        filmServiceImpl.remove(film);

        assertNotEquals(film, inMemoryFilmStorage.getAll().get(film.getId()), "Фильм был удалён");
    }

    @Test
    void shouldRemoveThrowNotFoundExceptionFilm() {
        Film film = new Film();

        NotFoundException thrown = assertThrows(NotFoundException.class,
                () -> filmServiceImpl.remove(film), "Не содержит фильм с таким " + film.getId());

        assertTrue(thrown.getMessage().contains("Не содержит фильм с таким " + film.getId()));
    }

    @Test
    void shouldUpdate() {
        Film film = new Film();
        film.setName("TestFilm");
        film.setDuration(90);
        film.setReleaseDate(LocalDate.of(2000, 5, 25));
        film.setDescription("About Test Film");

        filmServiceImpl.create(film);

        film.setName("TestFilm");
        film.setDuration(90);
        film.setReleaseDate(LocalDate.of(2000, 5, 25));
        film.setDescription("About Test Film");

        filmServiceImpl.update(film);

        assertEquals(film, inMemoryFilmStorage.getAll().get(film.getId()), "Обновили данные фильма");
    }

    @Test
    void shouldUpdateThrowNotFoundExceptionFilm() {
        Film film = new Film();
        film.setName("TestFilm");
        film.setDuration(90);
        film.setReleaseDate(LocalDate.of(2000, 5, 25));
        film.setDescription("About Test Film");

        NotFoundException thrown = assertThrows(NotFoundException.class,
                () -> filmServiceImpl.update(film), "Не содержит данный id");

        assertTrue(thrown.getMessage().contains("Не содержит данный id"));
    }

    @Test
    void shouldGetById() {
        Film film = new Film();
        film.setName("TestFilm");
        film.setDuration(90);
        film.setReleaseDate(LocalDate.of(2000, 5, 25));
        film.setDescription("About Test Film");

        filmServiceImpl.create(film);
        Film byId = filmServiceImpl.getById(film.getId());

        assertEquals(film, byId, "Фильмы идентичны");
    }

    @Test
    void shouldGetByIdThrowNotFoundExceptionFilm() {
        Film film = new Film();

        NotFoundException thrown = assertThrows(NotFoundException.class,
                () -> filmServiceImpl.getById(film.getId()), "Фильм не найден с таким id " + film.getId());

        assertTrue(thrown.getMessage().contains("Фильм не найден с таким id " + film.getId()));
    }

    @Test
    void shouldAddLike() {
        Film film = new Film();
        film.setName("TestFilm");
        film.setDuration(90);
        film.setReleaseDate(LocalDate.of(2000, 5, 25));
        film.setDescription("About Test Film");

        filmServiceImpl.create(film);

        User user = new User();
        user.setLogin("LoginTest");
        user.setBirthday(LocalDate.of(1995, 12, 20));
        user.setEmail("TestLogin1995@gmail.com");
        user.setName("NameTest");

        userServiceImpl.create(user);

        filmServiceImpl.addLike(film.getId(), user.getId());

        assertTrue(filmServiceImpl.getById(film.getId()).getLikes().contains(user.getId()),
                "Содержит лайк данного пользователя");
    }

    @Test
    void shouldAddLikeThrowNotFoundExceptionFilm() {
        Film film = new Film();

        User user = new User();
        user.setLogin("LoginTest");
        user.setBirthday(LocalDate.of(1995, 12, 20));
        user.setEmail("TestLogin1995@gmail.com");
        user.setName("NameTest");

        userServiceImpl.create(user);


        NotFoundException thrown = assertThrows(NotFoundException.class,
                () -> filmServiceImpl.addLike(film.getId(), user.getId()),
                "Фильм с таким id: " + film.getId() + " не найден");

        assertTrue(thrown.getMessage().contains("Фильм с таким id: " + film.getId() + " не найден"));
    }

    @Test
    void shouldAddLikeThrowNotFoundExceptionUser() {
        Film film = new Film();
        film.setName("TestFilm");
        film.setDuration(90);
        film.setReleaseDate(LocalDate.of(2000, 5, 25));
        film.setDescription("About Test Film");

        filmServiceImpl.create(film);

        User user = new User();

        NotFoundException thrown = assertThrows(NotFoundException.class,
                () -> filmServiceImpl.addLike(film.getId(), user.getId()),
                "Пользователь с таким id: " + user.getId() + " не найден");

        assertTrue(thrown.getMessage().contains("Пользователь с таким id: " + user.getId() + " не найден"));
    }

    @Test
    void shouldRemoveLike() {
        Film film = new Film();
        film.setName("TestFilm");
        film.setDuration(90);
        film.setReleaseDate(LocalDate.of(2000, 5, 25));
        film.setDescription("About Test Film");

        filmServiceImpl.create(film);

        User user = new User();
        user.setLogin("LoginTest");
        user.setBirthday(LocalDate.of(1995, 12, 20));
        user.setEmail("TestLogin1995@gmail.com");
        user.setName("NameTest");

        userServiceImpl.create(user);

        filmServiceImpl.addLike(film.getId(), user.getId());

        assertTrue(filmServiceImpl.getById(film.getId()).getLikes().contains(user.getId()),
                "Содержит лайк данного пользователя");

        filmServiceImpl.removeLike(film.getId(), user.getId());

        assertFalse(filmServiceImpl.getById(film.getId()).getLikes().contains(user.getId()),
                "Не содержит лайк данного пользователя");
    }

    @Test
    void shouldGetPopularFilms() {
        int film2Index = 2;
        long like1 = 1;
        long like2 = 2;
        long like3 = 3;
        long size3 = 3;
        long size15 = 15;

        Film film = new Film();
        film.setName("TestFilm");
        film.setDuration(90);
        film.setReleaseDate(LocalDate.of(2000, 5, 25));
        film.setDescription("About Test Film");
        film.getLikes().add(like1);
        film.getLikes().add(like2);
        film.getLikes().add(like3);

        filmServiceImpl.create(film);

        Film film1 = new Film();
        film1.setName("TestFilm1");
        film1.setDuration(90);
        film1.setReleaseDate(LocalDate.of(2000, 5, 25));
        film1.setDescription("About Test Film1");
        film1.getLikes().add(like1);
        film1.getLikes().add(like2);

        filmServiceImpl.create(film1);

        Film film2 = new Film();
        film2.setName("TestFilm");
        film2.setDuration(90);
        film2.setReleaseDate(LocalDate.of(2000, 5, 25));
        film2.setDescription("About Test Film");

        filmServiceImpl.create(film2);

        List<Film> popularFilms = filmServiceImpl.getPopularFilms(size3);

        assertEquals(film, popularFilms.getFirst(), "Первый фильм больше всего лайков (3)");
        assertEquals(film2, popularFilms.get(film2Index), "Последний фильм лайков (0)");

        popularFilms = filmServiceImpl.getPopularFilms(size15);

        int popularFilmsSize = 3;
        assertEquals(popularFilms.size(), popularFilmsSize, "Какой бы размер не указали вернет " +
                "отсортированные фильм которые были созданы");
    }

    @Test
    void shouldGetPopularFilmsNotFoundExceptionIsEmpty() {
        long size15 = 15;

        NotFoundException thrown = assertThrows(NotFoundException.class,
                () -> filmServiceImpl.getPopularFilms(size15),
                "Список фильмов пуст");

        assertTrue(thrown.getMessage().contains("Список фильмов пуст"));
    }

}