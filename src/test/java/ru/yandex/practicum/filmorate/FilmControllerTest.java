package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
    }

    @Test
    void shouldCreateValidFilm() {
        Film film = new Film();
        film.setName("Тестовый фильм");
        film.setDescription("Описание тестового фильма");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Film created = filmController.createFilm(film);

        assertNotNull(created.getId());
        assertEquals(1, created.getId());
        assertEquals("Тестовый фильм", created.getName());
        assertEquals(120, created.getDuration());
    }

    @Test
    void shouldNotCreateFilmWithEmptyName() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.createFilm(film));
        assertEquals("Название фильма не может быть пустым", exception.getMessage());
    }

    @Test
    void shouldNotCreateFilmWithNullName() {
        Film film = new Film();
        film.setName(null);
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.createFilm(film));
        assertEquals("Название фильма не может быть пустым", exception.getMessage());
    }

    @Test
    void shouldNotCreateFilmWithTooLongDescription() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("a".repeat(201));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.createFilm(film));
        assertEquals("Максимальная длина описания — 200 символов", exception.getMessage());
    }

    @Test
    void shouldNotCreateFilmWithDescriptionExactly201Chars() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("a".repeat(201));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    void shouldCreateFilmWithDescriptionExactly200Chars() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("a".repeat(200));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Film created = filmController.createFilm(film);
        assertEquals(200, created.getDescription().length());
    }

    @Test
    void shouldNotCreateFilmWithReleaseDateBeforeCinemaBirth() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(120);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.createFilm(film));
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    void shouldCreateFilmWithReleaseDateExactlyCinemaBirth() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(120);

        Film created = filmController.createFilm(film);
        assertEquals(LocalDate.of(1895, 12, 28), created.getReleaseDate());
    }

    @Test
    void shouldNotCreateFilmWithNegativeDuration() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(-10);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.createFilm(film));
        assertEquals("Продолжительность фильма должна быть положительным числом", exception.getMessage());
    }

    @Test
    void shouldNotCreateFilmWithZeroDuration() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(0);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.createFilm(film));
        assertEquals("Продолжительность фильма должна быть положительным числом", exception.getMessage());
    }

    @Test
    void shouldUpdateExistingFilm() {
        // Создаем фильм
        Film film = new Film();
        film.setName("Старое название");
        film.setDescription("Старое описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Film created = filmController.createFilm(film);

        // Обновляем фильм
        created.setName("Новое название");
        created.setDescription("Новое описание");

        Film updated = filmController.updateFilm(created);

        assertEquals("Новое название", updated.getName());
        assertEquals("Новое описание", updated.getDescription());
        assertEquals(1, filmController.getAllFilms().size());
    }

    @Test
    void shouldNotUpdateNonExistingFilm() {
        Film film = new Film();
        film.setId(999);
        film.setName("Фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.updateFilm(film));
        assertEquals("Фильм с id 999 не найден", exception.getMessage());
    }
}