package ru.yandex.practicum.filmorate.model.enums;

public enum Friendship {
    NOT_CONFIRMED("NOT_CONFIRMED"),
    CONFIRMED("CONFIRMED");

    private String value;

    Friendship(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
