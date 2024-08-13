package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.enums.Genres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Component
public class FilmResultSetMapper implements ResultSetExtractor<List<Film>> {

    @Override
    public List<Film> extractData(ResultSet rs) throws SQLException {
        List<Film> films = new ArrayList<>();

        boolean isNext = rs.next();

        while (isNext) {
            Film film = createFilm(rs);
            film.setMpa(createMpa(rs));
            isNext = addLikesAndGenres(rs, film);
            films.add(film);
        }

        return films;
    }

    private Film createFilm(ResultSet rs) throws SQLException {
        Film film = new Film();

        film.setId(rs.getLong("film.id"));
        film.setName(rs.getString("film.name"));
        film.setDescription(rs.getString("film.description"));
        film.setReleaseDate(rs.getDate("film.release_date").toLocalDate());
        film.setDuration(rs.getInt("film.duration"));

        return film;
    }

    private Mpa createMpa(ResultSet rs) throws SQLException {
        Mpa mpa = new Mpa();

        if (rs.getInt("mpa.id") != 0 && rs.getString("mpa.name") != null) {
            mpa.setId(rs.getInt("mpa.id"));
            mpa.setName(rs.getString("mpa.name"));
        }

        return mpa;
    }

    private Genre createGenre(ResultSet rs) throws SQLException {
        if (rs.getLong("genres.id") != 0 && rs.getString("genres.name") != null) {
            long genreId = rs.getLong("genres.id");
            String genreName = rs.getString("genres.name");

            return new Genre(genreId, Genres.valueOf(genreName));
        }
        return null;
    }

    private Like createLike(ResultSet rs) throws SQLException {
        if (rs.getLong("likes.id") != 0 && rs.getLong("likes.user_id") != 0
                && rs.getLong("likes.film_id") != 0) {
            long likeId = rs.getLong("likes.id");
            long userId = rs.getLong("likes.user_id");
            long filmId = rs.getLong("likes.film_id");

            return new Like(likeId, userId, filmId);
        }
        return null;
    }

    private boolean addLikesAndGenres(ResultSet rs, Film film) throws SQLException {
        List<Genre> genres = new ArrayList<>();
        List<Like> likes = new ArrayList<>();

        boolean isNext;

        do {
            Genre genre = createGenre(rs);
            if (genre != null) {
                genres.add(genre);
            }

            Like like = createLike(rs);
            if (like != null) {
                likes.add(like);
            }

            isNext = rs.next();
        } while (isNext && rs.getLong("film.id") == film.getId());

        film.setGenres(genres);
        film.setLikes(new HashSet<>(likes));

        return isNext;
    }
}