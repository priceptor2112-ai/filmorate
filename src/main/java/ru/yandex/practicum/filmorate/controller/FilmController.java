package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    private static final String LIKE_PATH = "/{id}/like/{userId}";
    private static final LocalDate EARLIEST_RELEASE_DATE = LocalDate.of(1895, Month.DECEMBER, 28);

    @Autowired
    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("GET /films - запрос на получение всех фильмов");
        return filmStorage.findAll();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        log.info("GET /films/{} - запрос на получение фильма", id);
        return filmStorage.findById(id);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("POST /films - запрос на создание фильма: {}", film);
        validateFilm(film);
        return filmStorage.create(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("PUT /films - запрос на обновление фильма: {}", film);
        validateFilm(film);
        return filmStorage.update(film);
    }

    @PutMapping(LIKE_PATH)
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        log.info("PUT /films/{}/like/{} - добавление лайка", id, userId);
        filmStorage.addLike(id, userId);
    }

    @DeleteMapping(LIKE_PATH)
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        log.info("DELETE /films/{}/like/{} - удаление лайка", id, userId);
        filmStorage.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("GET /films/popular?count={} - получение популярных фильмов", count);
        return filmStorage.getMostLikedFilms(count);
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("шибка валидации: название фильма не может быть пустым");
            throw new ValidationException("азвание фильма не может быть пустым");
        }

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.error("шибка валидации: длина описания превышает 200 символов");
            throw new ValidationException("аксимальная длина описания — 200 символов");
        }

        if (film.getReleaseDate() == null) {
            log.error("шибка валидации: дата релиза должна быть указана");
            throw new ValidationException("ата релиза должна быть указана");
        }

        if (film.getReleaseDate().isBefore(EARLIEST_RELEASE_DATE)) {
            log.error("шибка валидации: дата релиза {} раньше допустимой", film.getReleaseDate());
            throw new ValidationException("ата релиза не может быть раньше 28 декабря 1895 года");
        }

        if (film.getDuration() <= 0) {
            log.error("шибка валидации: продолжительность фильма {} должна быть положительной", film.getDuration());
            throw new ValidationException("родолжительность фильма должна быть положительным числом");
        }
    }
}