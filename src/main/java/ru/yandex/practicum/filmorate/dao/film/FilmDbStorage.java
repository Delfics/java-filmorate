package ru.yandex.practicum.filmorate.dao.film;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.enums.Genre;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;


@Repository
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;
    private final MpaRowMapper mpaRowMapper;
    private final GenreRowMapper genreRowMapper;
    private GeneratedKeyHolder keyHolder;

    @Override
    public List<Film> getAll() {
        String query = "SELECT * FROM film";
        List<Film> films = jdbcTemplate.query(query, filmRowMapper);
        for (Film film : films) {
            film.setMpa(getMpa(film.getId()));
            film.setGenres(getGenres(film.getId()));
            film.setLikes(getLikes(film.getId()));
        }
        return films;
    }

    @Override
    public Film create(Film film) {
        keyHolder = new GeneratedKeyHolder();

        int result = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement
                    ("INSERT INTO film (id, name, description, release_date, duration)" +
                                    " VALUES (NEXT VALUE FOR FILM_SEQ, ?, ?, ? ,?,);",
                            Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, film.getName());
            ps.setObject(2, film.getDescription());
            ps.setObject(3, film.getReleaseDate());
            ps.setObject(4, film.getDuration());

            return ps;
        }, keyHolder);

        Integer generatedId = keyHolder.getKeyAs(Integer.class);

        film.setId((long) generatedId);

        return film;
    }

    @Override
    public Film update(Film newFilm) {
        keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement
                    ("UPDATE INTO film (id, name, description, release_date, duration, mpa)" +
                                    " VALUES (?, ?, ?, ? ,?, &) WHERE id = ? ;",
                            Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, newFilm.getId());
            ps.setObject(2, newFilm.getName());
            ps.setObject(3, newFilm.getDescription());
            ps.setObject(4, newFilm.getReleaseDate());
            ps.setObject(5, newFilm.getDuration());
            ps.setLong(6, newFilm.getMpa().getId());
            ps.setLong(7, newFilm.getId());

            return ps;
        }, keyHolder);


        Integer generatedId = keyHolder.getKeyAs(Integer.class);

        newFilm.setId((long) generatedId);
        return newFilm;
    }

    @Override
    public Film getById(Long id) {
        String query = "SELECT * FROM film WHERE id = ?;";
        Film film = jdbcTemplate.queryForObject(query, filmRowMapper, id);

        Optional<Mpa> mpaForFilm = Optional.ofNullable(getMpa(film.getId()));
        mpaForFilm.ifPresent(film::setMpa);

        Optional<Set<Genre>> genresForFilm = Optional.ofNullable(getGenres(film.getId()));
        if (genresForFilm.isPresent()) {
            film.setGenres(getGenres(film.getId()));
        }

        Optional<Set<Long>> likesForFilm = Optional.ofNullable(getLikes(film.getId()));
        if (likesForFilm.isPresent()) {
            film.setLikes(getLikes(film.getId()));
        }
        return film;
    }

    @Override
    public boolean deleteById(Long id) {
        return jdbcTemplate.update("DELETE FROM film WHERE id = ?;", id) > 0;
    }

    @Override
    public Mpa getMpa(Long filmMpaId) {
        String query = "SELECT * FROM mpa WHERE id = ?;";
        return jdbcTemplate.queryForObject(query, mpaRowMapper, filmMpaId);
    }

    @Override
    public Set<Genre> getGenres(Long filmId) {
        String query = "SELECT name FROM genre WHERE id IN (" +
                "SELECT genre_id FROM film_genre WHERE film_id = ?);";
        /*List<Genre> genre = jdbcTemplate.query(query, genreRowMapper, filmId);*/
        /*return new HashSet<>(genre);*/
        return jdbcTemplate.queryForObject(query, Set.class, filmId);
    }

    @Override
    public Set<Long> getLikes (Long filmId) {
        String query = "SELECT user_id FROM likes WHERE film_id = ?;";
        return jdbcTemplate.queryForObject(query, Set.class, filmId);
    }
}
