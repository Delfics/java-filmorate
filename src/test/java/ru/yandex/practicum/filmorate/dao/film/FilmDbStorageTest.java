package ru.yandex.practicum.filmorate.dao.film;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.LikesRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase
@Import({FilmDbStorage.class, FilmRowMapper.class,
        MpaRowMapper.class, GenreRowMapper.class, LikesRowMapper.class})
class FilmDbStorageTest {
    @Autowired
    private FilmStorage filmDbStorage;

    @Test
    void getAll() {
    }

    @Test
    void create() {
    }

    @Test
    void update() {
    }

    @Test
    void getById() {
        long numb = 1;

        Optional<Film> userOptional = Optional.ofNullable(filmDbStorage.getById(numb));

        assertThat(userOptional).isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film).hasFieldOrPropertyWithValue("id", numb);
                });
    }

    @Test
    void deleteById() {
    }

    @Test
    void getMpa() {
    }

    @Test
    void getMpaByFilmId() {
    }

    @Test
    void getGenres() {
    }

    @Test
    void getGenresById() {
    }

    @Test
    void getLikes() {
    }

    @Test
    void addLike() {
    }

    @Test
    void deleteLike() {
    }
}