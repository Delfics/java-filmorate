package ru.yandex.practicum.filmorate.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Map;


public class ValidateUser {
    Map<Long, User> users;
    Logger log;

    public ValidateUser(Map<Long, User> users, Logger log) {
        this.users = users;
        this.log = log;
    }

    public void validate(User user) {
        String dog = "@";
        LocalDate data = LocalDate.now();
        if (user.getEmail() == null || !user.getEmail().contains(dog)) {
            log.error("Неправильно указана электронная почта");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        } else if (user.getLogin() == null || StringUtils.containsWhitespace(user.getLogin())) {
            log.error("Неправильно указан логин");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        } else if (user.getName() == null) {
            log.info("Имя не задано, оно будет равно логину - {}", user.getLogin());
            user.setName(user.getLogin());
        } else if (user.getBirthday().isAfter(data)) {
            log.error("Неправильно указана дата рождения");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

    public long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
