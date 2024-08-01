package ru.yandex.practicum.filmorate.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;


@JsonDeserialize(using = Genre.GenreDeserializer.class)
public enum Genre {
    COMEDY("COMEDY"),
    DRAMA("DRAMA"),
    CARTOON("CARTOON"),
    THRILLER("THRILLER"),
    DOCUMENTARY("DOCUMENTARY"),
    ACTION("ACTION");

    private String value;

    Genre(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static class GenreDeserializer extends JsonDeserializer<Genre> {
        @Override
        public Genre deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String value = p.getText();
            switch (value) {
                case "COMEDY":
                    return COMEDY;
                case "DRAMA":
                    return DRAMA;
                case "CARTOON":
                    return CARTOON;
                case "THRILLER":
                    return THRILLER;
                case "DOCUMENTARY":
                    return DOCUMENTARY;
                case "ACTION":
                    return ACTION;
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
