package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmController filmController;
    private InMemoryFilmStorage filmStorage;
    private InMemoryUserStorage userStorage;
    private FilmService filmService;

    @BeforeEach
    void setUp() {
        filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        filmService = new FilmService(filmStorage, userStorage);
        filmController = new FilmController(filmStorage, filmService);
    }

    // ==================== ТЕСТЫ СОЗДАНИЯ ФИЛЬМА ====================

    @Test
    void shouldCreateValidFilm() {
        Film film = createTestFilm("Тестовый фильм", "Описание", LocalDate.of(2000, 1, 1), 120);

        Film created = filmController.createFilm(film);

        assertNotNull(created.getId());
        assertEquals(1, created.getId());
        assertEquals("Тестовый фильм", created.getName());
        assertEquals("Описание", created.getDescription());
        assertEquals(LocalDate.of(2000, 1, 1), created.getReleaseDate());
        assertEquals(120, created.getDuration());
        assertNotNull(created.getLikes());
        assertEquals(0, created.getLikes().size());
    }

    @Test
    void shouldNotCreateFilmWithEmptyName() {
        Film film = createTestFilm("", "Описание", LocalDate.of(2000, 1, 1), 120);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.createFilm(film));
        assertEquals("Название фильма не может быть пустым", exception.getMessage());
    }

    @Test
    void shouldNotCreateFilmWithNullName() {
        Film film = createTestFilm(null, "Описание", LocalDate.of(2000, 1, 1), 120);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.createFilm(film));
        assertEquals("Название фильма не может быть пустым", exception.getMessage());
    }

    @Test
    void shouldNotCreateFilmWithNameOnlySpaces() {
        Film film = createTestFilm("   ", "Описание", LocalDate.of(2000, 1, 1), 120);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.createFilm(film));
        assertEquals("Название фильма не может быть пустым", exception.getMessage());
    }

    @Test
    void shouldNotCreateFilmWithTooLongDescription() {
        Film film = createTestFilm("Фильм", "a".repeat(201), LocalDate.of(2000, 1, 1), 120);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.createFilm(film));
        assertEquals("Максимальная длина описания — 200 символов", exception.getMessage());
    }

    @Test
    void shouldCreateFilmWithDescriptionExactly200Chars() {
        Film film = createTestFilm("Фильм", "a".repeat(200), LocalDate.of(2000, 1, 1), 120);

        Film created = filmController.createFilm(film);
        assertEquals(200, created.getDescription().length());
    }

    @Test
    void shouldNotCreateFilmWithReleaseDateBeforeCinemaBirth() {
        Film film = createTestFilm("Фильм", "Описание", LocalDate.of(1895, 12, 27), 120);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.createFilm(film));
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    void shouldCreateFilmWithReleaseDateExactlyCinemaBirth() {
        Film film = createTestFilm("Фильм", "Описание", LocalDate.of(1895, 12, 28), 120);

        Film created = filmController.createFilm(film);
        assertEquals(LocalDate.of(1895, 12, 28), created.getReleaseDate());
    }

    @Test
    void shouldCreateFilmWithReleaseDateAfterCinemaBirth() {
        Film film = createTestFilm("Фильм", "Описание", LocalDate.of(2000, 1, 1), 120);

        Film created = filmController.createFilm(film);
        assertEquals(LocalDate.of(2000, 1, 1), created.getReleaseDate());
    }

    @Test
    void shouldNotCreateFilmWithNegativeDuration() {
        Film film = createTestFilm("Фильм", "Описание", LocalDate.of(2000, 1, 1), -10);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.createFilm(film));
        assertEquals("Продолжительность фильма должна быть положительным числом", exception.getMessage());
    }

    @Test
    void shouldNotCreateFilmWithZeroDuration() {
        Film film = createTestFilm("Фильм", "Описание", LocalDate.of(2000, 1, 1), 0);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.createFilm(film));
        assertEquals("Продолжительность фильма должна быть положительным числом", exception.getMessage());
    }

    @Test
    void shouldNotCreateFilmWithNullReleaseDate() {
        Film film = createTestFilm("Фильм", "Описание", null, 120);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.createFilm(film));
        assertEquals("Дата релиза должна быть указана", exception.getMessage());
    }

    // ==================== ТЕСТЫ ОБНОВЛЕНИЯ ФИЛЬМА ====================

    @Test
    void shouldUpdateExistingFilm() {
        Film film = createTestFilm("Старое название", "Старое описание", LocalDate.of(2000, 1, 1), 120);
        Film created = filmController.createFilm(film);

        created.setName("Новое название");
        created.setDescription("Новое описание");
        created.setDuration(150);

        Film updated = filmController.updateFilm(created);

        assertEquals("Новое название", updated.getName());
        assertEquals("Новое описание", updated.getDescription());
        assertEquals(150, updated.getDuration());
        assertEquals(1, filmController.getAllFilms().size());
    }

    @Test
    void shouldNotUpdateNonExistingFilm() {
        Film film = createTestFilm("Фильм", "Описание", LocalDate.of(2000, 1, 1), 120);
        film.setId(999);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> filmController.updateFilm(film));
        assertEquals("Фильм с id 999 не найден", exception.getMessage());
    }

    @Test
    void shouldNotUpdateFilmWithInvalidData() {
        Film film = createTestFilm("Фильм", "Описание", LocalDate.of(2000, 1, 1), 120);
        Film created = filmController.createFilm(film);

        created.setName("");

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.updateFilm(created));
        assertEquals("Название фильма не может быть пустым", exception.getMessage());
    }

    // ==================== ТЕСТЫ ПОЛУЧЕНИЯ ФИЛЬМОВ ====================

    @Test
    void shouldGetAllFilms() {
        Film film1 = createTestFilm("Фильм 1", "Описание 1", LocalDate.of(2000, 1, 1), 120);
        Film film2 = createTestFilm("Фильм 2", "Описание 2", LocalDate.of(2001, 1, 1), 130);

        filmController.createFilm(film1);
        filmController.createFilm(film2);

        List<Film> allFilms = filmController.getAllFilms();

        assertEquals(2, allFilms.size());
    }

    @Test
    void shouldGetEmptyListWhenNoFilms() {
        List<Film> allFilms = filmController.getAllFilms();
        assertEquals(0, allFilms.size());
    }

    @Test
    void shouldGetFilmById() {
        Film film = createTestFilm("Фильм", "Описание", LocalDate.of(2000, 1, 1), 120);
        Film created = filmController.createFilm(film);

        Film found = filmController.getFilmById(created.getId());

        assertEquals(created.getId(), found.getId());
        assertEquals("Фильм", found.getName());
    }

    @Test
    void shouldThrowNotFoundWhenGettingNonExistingFilm() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> filmController.getFilmById(999));
        assertEquals("Фильм с id 999 не найден", exception.getMessage());
    }

    // ==================== ТЕСТЫ ЛАЙКОВ ====================

    @Test
    void shouldAddLikeToFilm() {
        Film film = createTestFilm("Фильм", "Описание", LocalDate.of(2000, 1, 1), 120);
        Film created = filmController.createFilm(film);

        // Создаём пользователя через userStorage напрямую для теста
        createTestUser(1, "user1@test.com", "user1", "User 1");

        filmController.addLike(created.getId(), 1);

        Film likedFilm = filmController.getFilmById(created.getId());
        assertEquals(1, likedFilm.getLikes().size());
        assertTrue(likedFilm.getLikes().contains(1));
    }

    @Test
    void shouldNotAddDuplicateLike() {
        Film film = createTestFilm("Фильм", "Описание", LocalDate.of(2000, 1, 1), 120);
        Film created = filmController.createFilm(film);

        createTestUser(1, "user1@test.com", "user1", "User 1");

        filmController.addLike(created.getId(), 1);
        filmController.addLike(created.getId(), 1);

        Film likedFilm = filmController.getFilmById(created.getId());
        assertEquals(1, likedFilm.getLikes().size());
    }

    @Test
    void shouldRemoveLikeFromFilm() {
        Film film = createTestFilm("Фильм", "Описание", LocalDate.of(2000, 1, 1), 120);
        Film created = filmController.createFilm(film);

        createTestUser(1, "user1@test.com", "user1", "User 1");

        filmController.addLike(created.getId(), 1);
        filmController.removeLike(created.getId(), 1);

        Film likedFilm = filmController.getFilmById(created.getId());
        assertEquals(0, likedFilm.getLikes().size());
    }

    @Test
    void shouldThrowNotFoundWhenAddingLikeToNonExistingFilm() {
        createTestUser(1, "user1@test.com", "user1", "User 1");

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> filmController.addLike(999, 1));
        assertEquals("Фильм с id 999 не найден", exception.getMessage());
    }

    // ==================== ТЕСТЫ ПОПУЛЯРНЫХ ФИЛЬМОВ ====================

    @Test
    void shouldGetPopularFilms() {
        Film film1 = createTestFilm("Фильм 1", "Описание 1", LocalDate.of(2000, 1, 1), 120);
        Film film2 = createTestFilm("Фильм 2", "Описание 2", LocalDate.of(2001, 1, 1), 130);
        Film film3 = createTestFilm("Фильм 3", "Описание 3", LocalDate.of(2002, 1, 1), 140);

        Film created1 = filmController.createFilm(film1);
        Film created2 = filmController.createFilm(film2);
        Film created3 = filmController.createFilm(film3);

        createTestUser(1, "user1@test.com", "user1", "User 1");
        createTestUser(2, "user2@test.com", "user2", "User 2");
        createTestUser(3, "user3@test.com", "user3", "User 3");

        // Фильм 1 получает 3 лайка
        filmController.addLike(created1.getId(), 1);
        filmController.addLike(created1.getId(), 2);
        filmController.addLike(created1.getId(), 3);

        // Фильм 2 получает 2 лайка
        filmController.addLike(created2.getId(), 1);
        filmController.addLike(created2.getId(), 2);

        // Фильм 3 получает 1 лайк
        filmController.addLike(created3.getId(), 1);

        List<Film> popular = filmController.getPopularFilms(10);

        assertEquals(3, popular.size());
        assertEquals(created1.getId(), popular.get(0).getId());
        assertEquals(created2.getId(), popular.get(1).getId());
        assertEquals(created3.getId(), popular.get(2).getId());
    }

    @Test
    void shouldReturnDefaultCountOfPopularFilms() {
        for (int i = 1; i <= 15; i++) {
            Film film = createTestFilm("Фильм " + i, "Описание", LocalDate.of(2000, 1, 1), 120);
            Film created = filmController.createFilm(film);

            createTestUser(i, "user" + i + "@test.com", "user" + i, "User " + i);
            filmController.addLike(created.getId(), i);
        }

        List<Film> popular = filmController.getPopularFilms(10);
        assertEquals(10, popular.size());
    }

    @Test
    void shouldReturnAllFilmsWhenCountGreaterThanTotal() {
        Film film1 = filmController.createFilm(createTestFilm("Фильм 1", "Описание 1", LocalDate.of(2000, 1, 1), 120));
        Film film2 = filmController.createFilm(createTestFilm("Фильм 2", "Описание 2", LocalDate.of(2001, 1, 1), 130));

        createTestUser(1, "user1@test.com", "user1", "User 1");
        filmController.addLike(film1.getId(), 1);

        List<Film> popular = filmController.getPopularFilms(10);
        assertEquals(2, popular.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoFilms() {
        List<Film> popular = filmController.getPopularFilms(10);
        assertEquals(0, popular.size());
    }

    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================

    private Film createTestFilm(String name, String description, LocalDate releaseDate, int duration) {
        Film film = new Film();
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);
        return film;
    }

    private void createTestUser(int id, String email, String login, String name) {
        ru.yandex.practicum.filmorate.model.User user = new ru.yandex.practicum.filmorate.model.User();
        user.setEmail(email);
        user.setLogin(login);
        user.setName(name);
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.create(user);
    }
}