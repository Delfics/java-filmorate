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

CREATE TABLE IF NOT EXISTS genre
(
    id   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR
);

CREATE TABLE IF NOT EXISTS film_genre
(
    id       INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_id  INTEGER REFERENCES film (id),
    genre_id INTEGER REFERENCES genre (id)
);

CREATE TABLE IF NOT EXISTS users
(
    id         INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email      VARCHAR NOT NULL,
    login      VARCHAR NOT NULL,
    name       VARCHAR,
    birthday   VARCHAR,
    friendship VARCHAR
);

CREATE TABLE IF NOT EXISTS friends
(
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY

);

-- ALTER TABLE users
--     ADD COLUMN IF NOT EXISTS friends_id INTEGER REFERENCES friends (id);

AlTER TABLE friends
    ADD COLUMN IF NOT EXISTS user_id INTEGER REFERENCES users (id);
AlTER TABLE friends
    ADD COLUMN IF NOT EXISTS friend_id INTEGER REFERENCES users (id);
AlTER TABLE friends
    ADD COLUMN IF NOT EXISTS friendship VARCHAR;

CREATE TABLE IF NOT EXISTS film_users
(
    id      INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_id INTEGER REFERENCES film (id),
    user_id INTEGER REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS friends_users
(
    id      INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id INTEGER REFERENCES users (id),
    friends_id INTEGER REFERENCES friends (id)
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
CREATE SEQUENCE film_genre_seq;
CREATE SEQUENCE genre_seq;

INSERT INTO mpa (id, name, description)
VALUES (NEXT VALUE FOR mpa_seq, 'G', ' — у фильма нет возрастных ограничений.'),
       (NEXT VALUE FOR mpa_seq, 'PG', ' — детям рекомендуется смотреть фильм с родителями.'),
       (NEXT VALUE FOR mpa_seq, 'PG-13', ' — детям до 13 лет просмотр не желателен.'),
       (NEXT VALUE FOR mpa_seq, 'R', ' — лицам до 17 лет просматривать фильм можно только в присутствии взрослого.'),
       (NEXT VALUE FOR mpa_seq, 'NC-17', ' — лицам до 18 лет просмотр запрещён.');








