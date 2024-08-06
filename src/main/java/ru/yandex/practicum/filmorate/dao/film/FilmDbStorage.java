package ru.yandex.practicum.filmorate.dao.film;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
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
            film.setGenres(getGenresByFilmId(film.getId()));
            if (film.getLikes() != null) {
                film.setLikes(getLikes(film.getId()));
            }
        }
        return films;
    }

    @Override
    public Film create(Film film) {
        keyHolder = new GeneratedKeyHolder();
        long zero = 0;
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

        if (film.getMpaId() != zero) {
            int result = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement
                        ("INSERT INTO film (id, name, description, release_date, duration, mpa_id)" +
                                        " VALUES (NEXT VALUE FOR FILM_SEQ, ?, ?, ? ,?, ?);",
                                Statement.RETURN_GENERATED_KEYS);
                ps.setObject(1, film.getName());
                ps.setObject(2, film.getDescription());
                ps.setObject(3, film.getReleaseDate());
                ps.setObject(4, film.getDuration());
                ps.setObject(5, film.getMpaId());


                return ps;
            }, keyHolder);

        } else {
            int result = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement
                        ("INSERT INTO film (id, name, description, release_date, duration)" +
                                        " VALUES (NEXT VALUE FOR FILM_SEQ, ?, ?, ? ,?);",
                                Statement.RETURN_GENERATED_KEYS);
                ps.setObject(1, film.getName());
                ps.setObject(2, film.getDescription());
                ps.setObject(3, film.getReleaseDate());
                ps.setObject(4, film.getDuration());


                return ps;
            }, keyHolder);

        }
        Integer generatedId = keyHolder.getKeyAs(Integer.class);

        film.setId((long) generatedId);
        if (film.getMpaId() != zero) {
            Mpa mpaByFilmId = getMpaByFilmId(film.getId());
            film.setMpa(mpaByFilmId);
        }

        return film;
    }

    @Override
    public Film update(Film newFilm) {
        keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement
                    ("UPDATE film SET id = ?, name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?" +
                                    "WHERE id = ? ;",
                            Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, newFilm.getId());
            ps.setObject(2, newFilm.getName());
            ps.setObject(3, newFilm.getDescription());
            ps.setObject(4, newFilm.getReleaseDate());
            ps.setObject(5, newFilm.getDuration());
            ps.setLong(6, newFilm.getMpaId());
            ps.setLong(7, newFilm.getId());

            return ps;
        }, keyHolder);
        if (!newFilm.getGenres().equals(getGenresByFilmId(newFilm.getId()))) {
            deleteGenresForFilm(newFilm.getId());
            for (Genre genre : newFilm.getGenres()) {
                updateGenresFromFilm(newFilm.getId(), genre.getId());
            }
            Set<Genre> genresByFilmId = getGenresByFilmId(newFilm.getId());
            newFilm.setGenres(genresByFilmId);
        }
        Integer generatedId = keyHolder.getKeyAs(Integer.class);

        newFilm.setId((long) generatedId);

        return newFilm;
    }

    @Override
    public Film getById(Long id) {
        try {
            String query = "SELECT * FROM film WHERE id = ?;";
            Film film = jdbcTemplate.queryForObject(query, filmRowMapper, id);

            if (film.getMpaId() != 0) {
                Mpa mpa = getMpaByFilmId(id);
                film.setMpa(mpa);
            }


            Optional<Set<Genre>> genresForFilm = Optional.ofNullable(getGenresByFilmId(film.getId()));
            if (genresForFilm.isPresent()) {
                film.setGenres(getGenresByFilmId(film.getId()));
            }

            Optional<Set<Like>> likesForFilm = Optional.ofNullable(getLikes(film.getId()));
            if (likesForFilm.isPresent()) {
                film.setLikes(getLikes(film.getId()));
            }
            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new  NotFoundException ("Фильм с таким id не найден");
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
    public Mpa getMpaById(Long MpaId) {
        try {
            String query = "SELECT * FROM mpa WHERE id = ?;";
            return jdbcTemplate.queryForObject(query, mpaRowMapper, MpaId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Нет Рейтинга с таким id " + MpaId);
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
        return  jdbcTemplate.query(query, genreRowMapper);
    }

    @Override
    public Set<Genre> getGenresByFilmId(Long filmId) {
        String query = "SELECT * FROM genres WHERE id IN (" +
                "SELECT genre_id FROM film_genres WHERE film_id = ?);";
        /*List<Genre> genre = jdbcTemplate.query(query, genreRowMapper, filmId);*/
        /*return new HashSet<>(genre);*/
        List<Genre> listGenres = jdbcTemplate.query(query, genreRowMapper, filmId);
        return new HashSet<>(listGenres);
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

    public void updateGenresFromFilm(Long filmId, Long genreId) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?);",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, filmId);
            ps.setObject(2, genreId);

            return ps;
        });
    }

    public boolean deleteGenresForFilm(Long filmId) {
     return jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?;", filmId) > 0;
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
