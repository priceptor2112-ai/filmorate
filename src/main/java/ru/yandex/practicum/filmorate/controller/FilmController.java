package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int currentId = 1;
    private static final LocalDate EARLIEST_RELEASE_DATE = LocalDate.of(1895, Month.DECEMBER, 28);

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("GET /films - запрос на получение всех фильмов");
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        log.info("POST /films - запрос на создание фильма: {}", film);
        validateFilm(film);

        film.setId(currentId++);
        films.put(film.getId(), film);

        log.info("Фильм успешно создан с ID {}: {}", film.getId(), film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        log.info("PUT /films - запрос на обновление фильма: {}", film);

        if (film.getId() <= 0) {
            log.error("ID фильма не указан или некорректен");
            throw new ValidationException("ID фильма должен быть указан");
        }

        if (!films.containsKey(film.getId())) {
            log.error("Фильм с ID {} не найден", film.getId());
            throw new ValidationException("Фильм с id " + film.getId() + " не найден");
        }

        validateFilm(film);
        films.put(film.getId(), film);

        log.info("Фильм успешно обновлен: {}", film);
        return film;
    }

    private void validateFilm(Film film) {
        // Проверка названия
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Ошибка валидации: название фильма не может быть пустым");
            throw new ValidationException("Название фильма не может быть пустым");
        }

        // Проверка описания
        if (film.getDescription() == null || film.getDescription().length() > 200) {
            log.error("Ошибка валидации: длина описания превышает 200 символов");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }

        // Проверка даты релиза
        if (film.getReleaseDate() == null) {
            log.error("Ошибка валидации: дата релиза должна быть указана");
            throw new ValidationException("Дата релиза должна быть указана");
        }

        if (film.getReleaseDate().isBefore(EARLIEST_RELEASE_DATE)) {
            log.error("Ошибка валидации: дата релиза {} раньше допустимой", film.getReleaseDate());
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        // Проверка продолжительности
        if (film.getDuration() <= 0) {
            log.error("Ошибка валидации: продолжительность фильма {} должна быть положительной", film.getDuration());
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }
}