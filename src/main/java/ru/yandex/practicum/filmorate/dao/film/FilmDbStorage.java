package ru.yandex.practicum.filmorate.dao.film;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.LikesRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
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
    private final LikesRowMapper likesRowMapper;
    private GeneratedKeyHolder keyHolder;

    @Override
    public List<Film> getAll() {
        String query = "SELECT * FROM film";
        List<Film> films = jdbcTemplate.query(query, filmRowMapper);
        for (Film film : films) {
            if (film.getMpa() != null) {
                film.setMpa(getMpaByFilmId(film.getMpa().getId()));
            }
            film.setGenres(getGenresById(film.getId()));
            if (film.getLikes() != null) {
                film.setLikes(getLikes(film.getId()));
            }
        }
        return films;
    }

    @Override
    public Film create(Film film) {
        keyHolder = new GeneratedKeyHolder();

        if (film.getMpa() == null) {
            int result = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement
                        ("INSERT INTO film (id, name, description, release_date, duration, mpa_id)" +
                                        " VALUES (NEXT VALUE FOR FILM_SEQ, ?, ?, ? ,?, NULL);",
                                Statement.RETURN_GENERATED_KEYS);
                ps.setObject(1, film.getName());
                ps.setObject(2, film.getDescription());
                ps.setObject(3, film.getReleaseDate());
                ps.setObject(4, film.getDuration());


                return ps;
            }, keyHolder);
        } else {
            int result = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement
                        ("INSERT INTO film (id, name, description, release_date, duration, mpa_id)" +
                                        " VALUES (NEXT VALUE FOR FILM_SEQ, ?, ?, ? ,?, ?);",
                                Statement.RETURN_GENERATED_KEYS);
                ps.setObject(1, film.getName());
                ps.setObject(2, film.getDescription());
                ps.setObject(3, film.getReleaseDate());
                ps.setObject(4, film.getDuration());
                ps.setObject(5, film.getMpa().getId());


                return ps;
            }, keyHolder);

        }
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

        if (film.getMpa() != null) {
            film.setMpa(getMpaByFilmId(film.getMpa().getId()));
        }

        Optional<Set<Genre>> genresForFilm = Optional.ofNullable(getGenresById(film.getId()));
        if (genresForFilm.isPresent()) {
            film.setGenres(getGenresById(film.getId()));
        }

        Optional<Set<Like>> likesForFilm = Optional.ofNullable(getLikes(film.getId()));
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
        List<Mpa> mpa = jdbcTemplate.query(query, mpaRowMapper);
        return mpa;
    }

    @Override
    public Mpa getMpaByFilmId(Long filmMpaId) {
        String query = "SELECT * FROM mpa WHERE id = ?;";
        return jdbcTemplate.queryForObject(query, mpaRowMapper, filmMpaId);
    }

    @Override
    public Set<Genre> getGenres() {
        String query = "SELECT * FROM genres;";
        List<Genre> listGenres =  jdbcTemplate.query(query, genreRowMapper);
        return new HashSet<>(listGenres);
    }

    @Override
    public Set<Genre> getGenresById(Long filmId) {
        String query = "SELECT name FROM genres WHERE id IN (" +
                "SELECT genre_id FROM film_genres WHERE film_id = ?);";
        /*List<Genre> genre = jdbcTemplate.query(query, genreRowMapper, filmId);*/
        /*return new HashSet<>(genre);*/
       List<Genre> listGenres =  jdbcTemplate.query(query, genreRowMapper, filmId);
        return new HashSet<>(listGenres);
    }

    @Override
    public Set<Like> getLikes(Long filmId) {
        String query = "SELECT * FROM likes WHERE film_id = ?;";
        List<Like> user_ids = jdbcTemplate.query(query, likesRowMapper, filmId);
        return new HashSet<>(user_ids);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
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
    public boolean deleteLike(Long filmId, Long userId) {
        return jdbcTemplate.update("DELETE FROM likes WHERE user_id = ?;", userId) > 0;
    }
}
