CREATE TABLE IF NOT EXISTS mpa
(
    id          INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name        VARCHAR,
    description VARCHAR
);

CREATE TABLE IF NOT EXISTS film
(
    id           INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         varchar NOT NULL,
    description  varchar,
    release_date varchar,
    duration     varchar NOT NULL,
    mpa_id       INTEGER REFERENCES mpa (id)
);

CREATE TABLE IF NOT EXISTS genres
(
    id   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR
);

CREATE TABLE IF NOT EXISTS film_genres
(
    id       INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_id  INTEGER REFERENCES film (id) ON DELETE CASCADE,
    genre_id INTEGER REFERENCES genres (id)
);

CREATE TABLE IF NOT EXISTS users
(
    id         INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email      VARCHAR NOT NULL,
    login      VARCHAR NOT NULL,
    name       VARCHAR,
    birthday   VARCHAR
);

CREATE TABLE IF NOT EXISTS friends
(
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY

);

AlTER TABLE friends
    ADD COLUMN IF NOT EXISTS user_id INTEGER REFERENCES users (id);
AlTER TABLE friends
    ADD COLUMN IF NOT EXISTS friend_id INTEGER REFERENCES users (id);
AlTER TABLE friends
    ADD COLUMN IF NOT EXISTS friendship VARCHAR;

CREATE TABLE IF NOT EXISTS film_users
(
    id      INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_id INTEGER REFERENCES film (id) ON DELETE CASCADE,
    user_id INTEGER REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS likes
(
    id      INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id INTEGER REFERENCES users (id),
    film_id INTEGER REFERENCES film (id)
);

CREATE SEQUENCE user_seq;
CREATE SEQUENCE film_seq;
CREATE SEQUENCE mpa_seq;
CREATE SEQUENCE likes_seq;
CREATE SEQUENCE film_users_seq;
CREATE SEQUENCE friends_seq;
CREATE SEQUENCE film_genres_seq;
CREATE SEQUENCE genre_seq;

INSERT INTO mpa (id, name)
VALUES (NEXT VALUE FOR mpa_seq, 'G'),
       (NEXT VALUE FOR mpa_seq, 'PG'),
       (NEXT VALUE FOR mpa_seq, 'PG-13'),
       (NEXT VALUE FOR mpa_seq, 'R'),
       (NEXT VALUE FOR mpa_seq, 'NC-17');

INSERT INTO genres (id, name)
VALUES (NEXT VALUE FOR genre_seq, 'Комедия'),
       (NEXT VALUE FOR genre_seq, 'Драма'),
       (NEXT VALUE FOR genre_seq, 'Мультфильм'),
       (NEXT VALUE FOR genre_seq, 'Триллер'),
       (NEXT VALUE FOR genre_seq, 'Документальный'),
       (NEXT VALUE FOR genre_seq, 'Боевик');













