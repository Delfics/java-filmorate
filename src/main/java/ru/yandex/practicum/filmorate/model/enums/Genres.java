package ru.yandex.practicum.filmorate.model.enums;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;


@JsonDeserialize(using = Genres.GenreDeserializer.class)
public enum Genres {
    Комедия("Комедия"),
    Драма("Драма"),
    Мультфильм("Мультфильм"),
    Триллер("Триллер"),
    Документальный("Документальный"),
    Боевик("Боевик");

    private String value;

    Genres(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static class GenreDeserializer extends JsonDeserializer<Genres> {
        @Override
        public Genres deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String value = p.getText();
            switch (value) {
                case "Комедия":
                    return Комедия;
                case "Драма":
                    return Драма;
                case "Мультфильм":
                    return Мультфильм;
                case "Триллер":
                    return Триллер;
                case "Документальный":
                    return Документальный;
                case "Боевик":
                    return Боевик;
            }
            return null;
        }
    }
//    COMEDY,
//    DRAMA,
//    CARTOON,
//    THRILLER,
//    DOCUMENTARY,
//    ACTION
}
