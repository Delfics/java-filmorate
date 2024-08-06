INSERT INTO film (id, name, description, release_date, duration, mpa_id)
VALUES (NEXT VALUE FOR film_seq, 'Терминатор', 'Про робота', '1994-11-19', '113', 3);

INSERT INTO film_genres (film_id, genre_id)
VALUES (1,6);

INSERT INTO users (id, email, login, name, birthday)
VALUES (NEXT VALUE FOR user_seq, 'baby95@mail.ru', 'SweetLady', 'NastyaBaby', '1999-09-25');

INSERT INTO users (id, email, login, name, birthday)
VALUES (NEXT VALUE FOR user_seq, 'BigBro92@mail.ru', 'BigBro', 'Sasha', '1992-09-25');

