package ru.yandex.practicum.filmorate.dao.film;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.practicum.filmorate.dal.mappers.*;
import ru.yandex.practicum.filmorate.dao.user.UserDbStorage;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.model.enums.Genres;
import ru.yandex.practicum.filmorate.service.FilmServiceImpl;
import ru.yandex.practicum.filmorate.service.UserServiceImpl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase
@Import({FilmServiceImpl.class, FilmDbStorage.class, FilmRowMapper.class,
        MpaRowMapper.class, GenreRowMapper.class, LikesRowMapper.class,
        UserServiceImpl.class, UserDbStorage.class, UserRowMapper.class, FriendRowMapper.class})
class FilmServiceImplTest {
    @Autowired
    private FilmServiceImpl filmServiceImpl;
    @Autowired
    private UserServiceImpl userServiceImpl;

    @Test
    void getAll() {
        List<Film> filmsLocal = new ArrayList<>();
        Film film = new Film();
        film.setId(1L);
        film.setName("Терминатор");
        film.setDescription("Про робота");
        film.setReleaseDate(LocalDate.of(1994, 11, 19));
        film.setDuration(113);
        film.setMpaId(3);
        Mpa mpaByFilmId = filmServiceImpl.getMpaByFilmId(film.getId());
        film.setMpa(mpaByFilmId);

        Genre genreById = filmServiceImpl.getGenreById(6L);

        Set<Genre> genres = new HashSet<>();
        genres.add(genreById);

        film.setGenres(genres);

        filmsLocal.add(film);

        List<Film> filmsGot = filmServiceImpl.getAllValues();

        assertEquals(filmsLocal, filmsGot, "Содержит идентичные списки фильмов");

    }


    @Test
    void create() {
        long numb = 2;
        String name = "Дюна";
        String description = "Про червяков";
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate releaseDate = LocalDate.of(1999, 11, 19);
        int duration = 211;
        long mpaFilmId = 4;
        Set<Genre> genres = new HashSet<>();
        Genre genre = new Genre();
        genre.setId(6);
        genre.setName(Genres.Боевик);
        genres.add(genre);

        Film film = new Film();
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);
        film.setMpaId(mpaFilmId);
        film.setGenres(genres);


        Optional<Film> filmOptional = Optional.ofNullable(filmServiceImpl.create(film));
        Mpa mpa = filmServiceImpl.getMpaByFilmId(film.getId());

        assertThat(filmOptional).isPresent()
                .hasValueSatisfying(film1 -> {
                    assertThat(film1).hasFieldOrPropertyWithValue("id", numb);
                    assertThat(film1).hasFieldOrPropertyWithValue("name", name);
                    assertThat(film1).hasFieldOrPropertyWithValue("description", description);
                    assertThat(film1).hasFieldOrPropertyWithValue("releaseDate", releaseDate);
                    assertThat(film1).hasFieldOrPropertyWithValue("duration", duration);
                    assertThat(film1).hasFieldOrPropertyWithValue("mpaId", mpaFilmId);
                    assertThat(film1).hasFieldOrPropertyWithValue("mpa", mpa);
                    assertThat(film1).hasFieldOrPropertyWithValue("genres", genres);

                });
    }

    @Test
    void update() {
        long filmId = 1;
        Film byId = filmServiceImpl.getById(filmId);

        String name = "Терминатор 2";
        String description = "Про робота киборга";
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate releaseDate = LocalDate.of(1999, 12, 19);
        int duration = 211;
        long mpaFilmId = 5;

        Genre genre = new Genre();
        genre.setId(2);
        genre.setName(Genres.Драма);
        Set<Genre> genres = new HashSet<>();
        genres.add(genre);

        Film film = new Film();
        film.setId(byId.getId());
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);
        film.setMpaId(mpaFilmId);
        film.setGenres(genres);

        byId.setName(name);
        byId.setDescription(description);
        byId.setReleaseDate(releaseDate);
        byId.setDuration(duration);
        byId.setMpaId(mpaFilmId);
        byId.setGenres(genres);

        filmServiceImpl.update(byId);

        byId.setMpa(filmServiceImpl.getMpaByFilmId(byId.getId()));
        film.setMpa(filmServiceImpl.getMpaByFilmId(film.getId()));


        Film byId1 = filmServiceImpl.getById(byId.getId());

        assertEquals(film, byId1, "Фильм обновлён");

    }

    @Test
    void getById() {
        long numb = 1;
        String name = "Терминатор";
        String description = "Про робота";
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate releaseDate = LocalDate.of(1994, 11, 19);
        int duration = 113;
        long mpaFilmId = 3;

        LocalDate release_dateFormat = LocalDate.parse(releaseDate.format(format));

        Mpa mpa = filmServiceImpl.getMpaByFilmId(numb);
        Set<Like> likes = filmServiceImpl.getLikes(numb);
        Set<Genre> genres = filmServiceImpl.getGenresByFilmId(numb);

        Optional<Film> filmOptional = Optional.ofNullable(filmServiceImpl.getById(numb));

        assertThat(filmOptional).isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film).hasFieldOrPropertyWithValue("id", numb);
                    assertThat(film).hasFieldOrPropertyWithValue("name", name);
                    assertThat(film).hasFieldOrPropertyWithValue("description", description);
                    assertThat(film).hasFieldOrPropertyWithValue("releaseDate", release_dateFormat);
                    assertThat(film).hasFieldOrPropertyWithValue("duration", duration);
                    assertThat(film).hasFieldOrPropertyWithValue("likes", likes);
                    assertThat(film).hasFieldOrPropertyWithValue("genres", genres);
                    assertThat(film).hasFieldOrPropertyWithValue("mpaId", mpaFilmId);
                    assertThat(film).hasFieldOrPropertyWithValue("mpa", mpa);
                });
    }

    @Test
    void deleteById() {
        String name = "Звездные Войны";
        String description = "Про инопланетян";
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate releaseDate = LocalDate.of(1990, 11, 19);
        int duration = 177;
        long mpaFilmId = 4;
        Set<Genre> genres = new HashSet<>();
        Genre genre = new Genre();
        genre.setId(6);
        genre.setName(Genres.Боевик);
        genres.add(genre);

        Film film = new Film();
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);
        film.setMpaId(mpaFilmId);
        film.setGenres(genres);


        Optional<Film> filmOptional = Optional.ofNullable(filmServiceImpl.create(film));

        filmServiceImpl.deleteById(filmOptional.get().getId());

        List<Film> all = filmServiceImpl.getAllValues();
        boolean contains = all.contains(filmOptional.get());

        assertFalse(contains, "Фильма не существует");

    }

    @Test
    void getMpa() {
        List<Mpa> localMpa = new ArrayList<>();
        Mpa mpa1 = new Mpa();
        mpa1.setId(1);
        mpa1.setName("G");

        Mpa mpa2 = new Mpa();
        mpa2.setId(2);
        mpa2.setName("PG");

        Mpa mpa3 = new Mpa();
        mpa3.setId(3);
        mpa3.setName("PG-13");

        Mpa mpa4 = new Mpa();
        mpa4.setId(4);
        mpa4.setName("R");

        Mpa mpa5 = new Mpa();
        mpa5.setId(5);
        mpa5.setName("NC-17");

        localMpa.add(mpa1);
        localMpa.add(mpa2);
        localMpa.add(mpa3);
        localMpa.add(mpa4);
        localMpa.add(mpa5);

        List<Mpa> mpa = filmServiceImpl.getMpa();

        assertEquals(localMpa, mpa, "Рейтинг возрастов идентичен");
    }

    @Test
    void getMpaByFilmId() {
        long id = 1;

        long mpaId = 3;
        String name = "PG-13";
        Mpa mpa = new Mpa();
        mpa.setId(mpaId);
        mpa.setName(name);


        Mpa mpaById = filmServiceImpl.getMpaByFilmId(id);

        assertEquals(mpa, mpaById, "Mpa равны друг другу");
    }

    @Test
    void getGenres() {
        Genre genre1 = new Genre();
        genre1.setId(1);
        genre1.setName(Genres.Комедия);

        Genre genre2 = new Genre();
        genre2.setId(2);
        genre2.setName(Genres.Драма);

        Genre genre3 = new Genre();
        genre3.setId(3);
        genre3.setName(Genres.Мультфильм);

        Genre genre4 = new Genre();
        genre4.setId(4);
        genre4.setName(Genres.Триллер);

        Genre genre5 = new Genre();
        genre5.setId(5);
        genre5.setName(Genres.Документальный);

        Genre genre6 = new Genre();
        genre6.setId(6);
        genre6.setName(Genres.Боевик);

        List<Genre> genresLocal = new ArrayList<>();
        genresLocal.add(genre1);
        genresLocal.add(genre2);
        genresLocal.add(genre3);
        genresLocal.add(genre4);
        genresLocal.add(genre5);
        genresLocal.add(genre6);


        List<Genre> genres = filmServiceImpl.getGenres();

        assertEquals(genresLocal, genres, "Жанры идентичны");
    }

    @Test
    void getGenresById() {
        Genre genre1 = new Genre();
        genre1.setId(6);
        genre1.setName(Genres.Боевик);


        Genre genreById = filmServiceImpl.getGenreById(genre1.getId());

        assertEquals(genre1, genreById, "Жанры идентичены");

    }

    @Test
    void getLikes() {

        long userId = 1;
        long filmId = 1;
        Like like = new Like();
        Set<Like> likesLocal = new HashSet<>();

        like.setId(3);
        like.setUserId(userId);
        like.setFilmId(filmId);

        likesLocal.add(like);


        User userById = userServiceImpl.getById(userId);
        Film filmById = filmServiceImpl.getById(filmId);

        filmServiceImpl.addLike(filmById.getId(), userById.getId());
        Set<Like> likesByFilm = filmServiceImpl.getLikes(filmById.getId());

        assertEquals(likesLocal, likesByFilm, "Содержит лайк от пользователя");

        Set<Like> likesGetAll = filmServiceImpl.getLikes(filmById.getId());

        assertEquals(likesLocal, likesGetAll, "Содержит все лайки");
    }

    @Test
    void addLike() {
        long userId = 1;
        long filmId = 1;
        Like like = new Like();
        Set<Like> likes = new HashSet<>();

        like.setId(1);
        like.setUserId(userId);
        like.setFilmId(filmId);

        likes.add(like);


        User userById = userServiceImpl.getById(userId);
        Film filmById = filmServiceImpl.getById(filmId);

        filmServiceImpl.addLike(filmById.getId(), userById.getId());
        Set<Like> likesByFilm = filmServiceImpl.getLikes(filmById.getId());

        assertEquals(likes, likesByFilm, "Содержит лайк от пользователя");
    }

    @Test
    void deleteLike() {
        long userId = 1;
        long filmId = 1;
        Like like = new Like();
        Set<Like> likes = new HashSet<>();

        like.setId(2);
        like.setUserId(userId);
        like.setFilmId(filmId);

        likes.add(like);

        User userById = userServiceImpl.getById(userId);
        Film filmById = filmServiceImpl.getById(filmId);

        filmServiceImpl.addLike(filmById.getId(), userById.getId());
        Set<Like> likesByFilm = filmServiceImpl.getLikes(filmById.getId());

        assertEquals(likes, likesByFilm, "Содержит лайк от пользователя");

        filmServiceImpl.deleteLike(filmById.getId(), userById.getId());
        Set<Like> likesMustEmpty = filmServiceImpl.getLikes(filmById.getId());

        assertNotEquals(likes, likesMustEmpty, "Не содержит лайк от пользователя");
    }
}
