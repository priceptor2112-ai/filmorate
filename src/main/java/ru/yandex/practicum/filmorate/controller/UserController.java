package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int currentId = 1;

    @GetMapping
    public List<User> getAllUsers() {
        log.info("GET /users - запрос на получение всех пользователей");
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        log.info("POST /users - запрос на создание пользователя: {}", user);
        validateUser(user);

        // Если имя пустое, используем логин
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя не указано, используется логин: {}", user.getLogin());
        }

        user.setId(currentId++);
        users.put(user.getId(), user);

        log.info("Пользователь успешно создан с ID {}: {}", user.getId(), user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        log.info("PUT /users - запрос на обновление пользователя: {}", user);

        if (user.getId() <= 0) {
            log.error("ID пользователя не указан или некорректен");
            throw new ValidationException("ID пользователя должен быть указан");
        }

        if (!users.containsKey(user.getId())) {
            log.error("Пользователь с ID {} не найден", user.getId());
            throw new ValidationException("Пользователь с id " + user.getId() + " не найден");
        }

        validateUser(user);

        // Если имя пустое, используем логин
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя не указано, используется логин: {}", user.getLogin());
        }

        users.put(user.getId(), user);

        log.info("Пользователь успешно обновлен: {}", user);
        return user;
    }

    private void validateUser(User user) {
        // Проверка email
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.error("Ошибка валидации: email не может быть пустым");
            throw new ValidationException("Email не может быть пустым");
        }

        if (!user.getEmail().contains("@")) {
            log.error("Ошибка валидации: email {} не содержит @", user.getEmail());
            throw new ValidationException("Email должен содержать символ @");
        }

        // Проверка логина
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.error("Ошибка валидации: логин не может быть пустым");
            throw new ValidationException("Логин не может быть пустым");
        }

        if (user.getLogin().contains(" ")) {
            log.error("Ошибка валидации: логин {} содержит пробелы", user.getLogin());
            throw new ValidationException("Логин не может содержать пробелы");
        }

        // Проверка даты рождения
        if (user.getBirthday() == null) {
            log.error("Ошибка валидации: дата рождения должна быть указана");
            throw new ValidationException("Дата рождения должна быть указана");
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Ошибка валидации: дата рождения {} в будущем", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}