package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


class ValidateUserTest {
    User user;

    @Test
    void shouldGetWrongEmail() {
        user = new User();
        user.setName("Name");
        user.setEmail("gmail.com");
        user.setLogin("NameUser");
        user.setBirthday(LocalDate.of(1999, 10, 23));

        ValidationException thrown = assertThrows(ValidationException.class,
                () -> ValidateUser.validate(user), "Электронная почта не может быть пустой и должна" +
                        " содержать символ @");

        assertTrue(thrown.getMessage().contains("Электронная почта не может быть пустой и должна содержать символ @"));
    }

    @Test
    void shouldGetNullEmail() {
        user = new User();
        user.setName("Name");
        user.setLogin("NameUser");
        user.setBirthday(LocalDate.of(1999, 10, 23));

        ValidationException thrown = assertThrows(ValidationException.class,
                () -> ValidateUser.validate(user), "Электронная почта не может быть пустой и должна" +
                        " содержать символ @");

        assertTrue(thrown.getMessage().contains("Электронная почта не может быть пустой и должна содержать символ @"));
    }

    @Test
    void shouldGetLoginWithWhiteSpace() {
        user = new User();
        user.setName("Name");
        user.setEmail("@gmail.com");
        user.setLogin("Na me Use r");
        user.setBirthday(LocalDate.of(1999, 10, 23));

        ValidationException thrown = assertThrows(ValidationException.class,
                () -> ValidateUser.validate(user), "Логин не может быть пустым и содержать пробелы");

        assertTrue(thrown.getMessage().contains("Логин не может быть пустым и содержать пробелы"));
    }

    @Test
    void shouldGetLoginNull() {
        user = new User();
        user.setName("Name");
        user.setEmail("@gmail.com");
        user.setBirthday(LocalDate.of(1999, 10, 23));

        ValidationException thrown = assertThrows(ValidationException.class,
                () -> ValidateUser.validate(user), "Логин не может быть пустым и содержать пробелы");

        assertTrue(thrown.getMessage().contains("Логин не может быть пустым и содержать пробелы"));
    }

    @Test
    void shouldGetWrongBirthday() {
        user = new User();
        user.setName("Name");
        user.setEmail("@gmail.com");
        user.setLogin("NameUser");
        user.setBirthday(LocalDate.of(2026, 10, 23));

        ValidationException thrown = assertThrows(ValidationException.class,
                () -> ValidateUser.validate(user), "Дата рождения не может быть в будущем");

        assertTrue(thrown.getMessage().contains("Дата рождения не может быть в будущем"));

    }

    @Test
    void shouldSetNameIntoEmptyLogin() {
        user = new User();
        user.setLogin("LoginTest");
        user.setEmail("@gmail.com");
        user.setBirthday(LocalDate.of(2026, 10, 23));
        ValidateUser.validate(user);

        assertEquals(user.getName(), user.getLogin(), "При пустом поле name, login будет name");
    }

}