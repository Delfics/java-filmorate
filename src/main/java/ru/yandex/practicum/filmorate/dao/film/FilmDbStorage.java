package ru.yandex.practicum.filmorate.dao.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmResultSetMapper;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.LikesRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmResultSetMapper filmResultSetMapper;
    private final MpaRowMapper mpaRowMapper;
    private final GenreRowMapper genreRowMapper;
    private final LikesRowMapper likesRowMapper;
    private GeneratedKeyHolder keyHolder;


    @Override
    public List<Film> getAll() {
        String query = "SELECT DISTINCT film.id, film.name, film.DESCRIPTION, film.RELEASE_DATE, film.DURATION, " +
                "       mpa.ID, mpa.NAME, likes.id, likes.USER_ID, likes.FILM_ID, genres.ID, genres.NAME " +
                "FROM film " +
                "                    LEFT JOIN mpa ON film.mpa_id = mpa.id " +
                "                    LEFT JOIN likes ON film.id = likes.film_id " +
                "                    LEFT JOIN film_genres ON film_genres.film_id = film.id " +
                "                    LEFT JOIN genres ON film_genres.genre_id = genres.id ;";
        return jdbcTemplate.query(query, filmResultSetMapper);
    }

    @Override
    public Film create(Film film) {
        keyHolder = new GeneratedKeyHolder();
        byte six = 6;
        byte one = 1;

        if (film.getMpa() != null) {
            if (film.getMpa().getId() > six || film.getMpa().getId() < one) {
                throw new ValidationException("Mpa не существует с таким id");
            }
        }

        for (Genre genre : film.getGenres()) {
            if (genre.getId() > six || genre.getId() < one) {
                throw new ValidationException("Genre не существует с таким id");
            }
        }

        int result;

        if (film.getMpa() != null) {
            result = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO film " +
                                "(id, name, description, release_date, duration, mpa_id)" +
                                " VALUES (NEXT VALUE FOR FILM_SEQ, ?, ?, ? ,?, ?);",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setObject(1, film.getName());
                ps.setObject(2, film.getDescription());
                ps.setObject(3, film.getReleaseDate());
                ps.setObject(4, film.getDuration());
                ps.setObject(5, film.getMpa().getId());

                return ps;
            }, keyHolder);

        } else {
            result = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO film " +
                                "(id, name, description, release_date, duration)" +
                                " VALUES (NEXT VALUE FOR FILM_SEQ, ?, ?, ? ,?);",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setObject(1, film.getName());
                ps.setObject(2, film.getDescription());
                ps.setObject(3, film.getReleaseDate());
                ps.setObject(4, film.getDuration());

                return ps;
            }, keyHolder);

        }
        if (result < 1) {
            throw new NotFoundException("Фильм " + film.getName() + " не создан");
        }

        Integer generatedId = keyHolder.getKeyAs(Integer.class);

        film.setId((long) generatedId);
        if (film.getMpa() != null) {
            Mpa mpaByFilmId = getMpaByFilmId(film.getId());
            film.setMpa(mpaByFilmId);
        }

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                updateGenresFromFilm(film.getId(), genre.getId());
            }
            film.setGenres(getGenresByFilmId(film.getId()));
        }

        return film;
    }

    @Override
    public Film update(Film newFilm) {
        keyHolder = new GeneratedKeyHolder();

        int result = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("UPDATE film " +
                            "SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?" +
                            "WHERE id = ? ;",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, newFilm.getName());
            ps.setObject(2, newFilm.getDescription());
            ps.setObject(3, newFilm.getReleaseDate());
            ps.setObject(4, newFilm.getDuration());
            ps.setLong(5, newFilm.getMpa().getId());
            ps.setLong(6, newFilm.getId());

            return ps;
        }, keyHolder);

        if (result < 1) {
            throw new NotFoundException("Фильм " + newFilm.getName() + " не создан");
        }

        if (!newFilm.getGenres().equals(getGenresByFilmId(newFilm.getId()))) {
            deleteGenresForFilm(newFilm.getId());
            for (Genre genre : newFilm.getGenres()) {
                updateGenresFromFilm(newFilm.getId(), genre.getId());
            }
            /*batchUpdateGenres(newFilm);*/
            List<Genre> genresByFilmId = getGenresByFilmId(newFilm.getId());
            newFilm.setGenres(genresByFilmId);
        }
        Integer generatedId = keyHolder.getKeyAs(Integer.class);

        newFilm.setId((long) generatedId);

        return newFilm;
    }

    @Override
    public Film getById(Long id) {
        try {
            String query = "SELECT DISTINCT film.id, film.name, film.DESCRIPTION, film.RELEASE_DATE, film.DURATION, " +
                    "       mpa.ID, mpa.NAME, LIKES.id, LIKES.USER_ID, LIKES.FILM_ID, GENRES.ID, GENRES.NAME " +
                    "FROM film " +
                    "                    LEFT JOIN mpa ON film.mpa_id = mpa.id " +
                    "                    LEFT JOIN likes  ON film.id = likes.film_id " +
                    "                    LEFT JOIN film_genres ON film_genres.film_id = film.id " +
                    "                    LEFT JOIN genres ON film_genres.genre_id = genres.id " +
                    "                    WHERE film.id = ? ;";


            return jdbcTemplate.query(query, filmResultSetMapper, id).get(0);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильм с таким id не найден");
        }
    }

    @Override
    public boolean deleteById(Long id) {
        return jdbcTemplate.update("DELETE FROM film WHERE id = ?;", id) > 0;
    }

    @Override
    public List<Mpa> getMpa() {
        String query = "SELECT * FROM mpa";
        return jdbcTemplate.query(query, mpaRowMapper);
    }

    @Override
    public Mpa getMpaById(Long mpaId) {
        try {
            String query = "SELECT * FROM mpa WHERE id = ?;";
            return jdbcTemplate.queryForObject(query, mpaRowMapper, mpaId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Нет Рейтинга с таким id " + mpaId);
        }
    }

    @Override
    public Mpa getMpaByFilmId(Long filmId) {
        try {
            String query = "SELECT * FROM mpa WHERE id IN (SELECT mpa_id FROM film WHERE id = ?);";
            return jdbcTemplate.queryForObject(query, mpaRowMapper, filmId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Mpa для фильма с id " + filmId + " не найден");
        }
    }

    @Override
    public List<Genre> getGenres() {
        String query = "SELECT * FROM genres;";
        return jdbcTemplate.query(query, genreRowMapper);
    }

    @Override
    public List<Genre> getGenresByFilmId(Long filmId) {
        String query = "SELECT * FROM genres WHERE id IN (" +
                "SELECT genre_id FROM film_genres WHERE film_id = ?);";
        return jdbcTemplate.query(query, genreRowMapper, filmId);
    }

    @Override
    public Genre getGenreById(Long genreId) {
        try {
            String query = "SELECT * FROM genres WHERE id = ?;";
            return jdbcTemplate.queryForObject(query, genreRowMapper, genreId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Нет жанра с таким id " + genreId);
        }
    }

    private void updateGenresFromFilm(Long filmId, Long genreId) {
        String checkSql = "SELECT COUNT(*) FROM film_genres WHERE film_id = ? AND genre_id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, filmId, genreId);

        if (count == null || count == 0) {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO film_genres (id, film_id, genre_id) VALUES" +
                                " (NEXT VALUE FOR film_genres_seq, ?, ?);",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setObject(1, filmId);
                ps.setObject(2, genreId);

                return ps;
            });
        } else {
            log.warn("Запись уже существует для film_id: {} и genre_id: {}", filmId, genreId);
        }
    }

    private boolean deleteGenresForFilm(Long filmId) {
        return jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?;", filmId) > 0;
    }

    @Override
    public Set<Like> getLikes(Long filmId) {
        String query = "SELECT * FROM likes WHERE film_id = ?;";
        List<Like> userIds = jdbcTemplate.query(query, likesRowMapper, filmId);
        return new HashSet<>(userIds);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO likes (id, user_id, film_id)" +
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

    public boolean filmExists(Long filmId) {
        String sql = "SELECT EXISTS (SELECT 1 FROM films WHERE film_id = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, filmId);
    }

    public void batchUpdateGenres(Film film) {
            jdbcTemplate.update(connection -> {
                try {
                    connection.setAutoCommit(false);
                    PreparedStatement ps = connection.prepareStatement(
                            "INSERT INTO film_genres (id, film_id, genre_id) VALUES" +
                                    " (NEXT VALUE FOR film_genres_seq, ?, ?);",
                            Statement.RETURN_GENERATED_KEYS);
                    for (Genre genre : film.getGenres()) {
                        ps.setObject(1, film.getId());
                        ps.setObject(2, genre.getId());
                        ps.addBatch();
                    }

                    int[] updateCount = ps.executeBatch();
                    connection.commit();
                    return ps;
                } catch (SQLException e) {
                    connection.rollback();
                    e.printStackTrace();
                    return null;
                }
            });
    }
}
