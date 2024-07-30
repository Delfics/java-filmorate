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
            film.setMpa(getMpaByFilmId(film.getId()));
            film.setGenres(getGenresById(film.getId()));
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
                    ("UPDATE film SET id = ?, name = ?, description = ?, release_date = ?, duration = ?, mpa = ?" +
                                    "WHERE id = ? ;",
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

        Optional<Mpa> mpaForFilm = Optional.ofNullable(getMpaByFilmId(film.getId()));
        mpaForFilm.ifPresent(film::setMpa);

        Optional<Set<Genre>> genresForFilm = Optional.ofNullable(getGenresById(film.getId()));
        if (genresForFilm.isPresent()) {
            film.setGenres(getGenresById(film.getId()));
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
    public List<Mpa> getMpa() {
        String query = "SELECT * FROM mpa";
       List <Mpa> mpa = jdbcTemplate.queryForObject(query, List.class, mpaRowMapper);
       return mpa;
    }

    @Override
    public Mpa getMpaByFilmId(Long filmMpaId) {
        String query = "SELECT * FROM mpa WHERE id = ?;";
        return jdbcTemplate.queryForObject(query, mpaRowMapper, filmMpaId);
    }

    @Override
    public Set<Genre> getGenres () {
        String query = "SELECT * FROM genres;";
        return jdbcTemplate.queryForObject(query, Set.class, genreRowMapper);
    }

    @Override
    public Set<Genre> getGenresById(Long filmId) {
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

    @Override
    public void addLike (Long filmId, Long userId) {
        keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement
                    ("INSERT INTO likes (id, user_id, film_id)" +
                                    " VALUES (NEXT VALUE FOR likes_seq, ?, ?);",
                            Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, userId);
            ps.setObject(2, filmId);

            return ps;
        }, keyHolder);
    }

    @Override
    public boolean deleteLike (Long filmId, Long userId) {
        return jdbcTemplate.update("DELETE FROM likes WHERE user_id = ?;", userId) > 0;
    }
}
