package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    @Test
    void shouldCreateValidUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User created = userController.createUser(user);

        assertNotNull(created.getId());
        assertEquals(1, created.getId());
        assertEquals("test@example.com", created.getEmail());
        assertEquals("testlogin", created.getLogin());
        assertEquals("Test User", created.getName());
    }

    @Test
    void shouldSetNameAsLoginWhenNameIsEmpty() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setName("");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User created = userController.createUser(user);
        assertEquals("testlogin", created.getName());
    }

    @Test
    void shouldSetNameAsLoginWhenNameIsNull() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setName(null);
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User created = userController.createUser(user);
        assertEquals("testlogin", created.getName());
    }

    @Test
    void shouldSetNameAsLoginWhenNameIsBlank() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setName("   ");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User created = userController.createUser(user);
        assertEquals("testlogin", created.getName());
    }

    @Test
    void shouldNotCreateUserWithEmptyEmail() {
        User user = new User();
        user.setEmail("");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.createUser(user));
        assertEquals("Email не может быть пустым", exception.getMessage());
    }

    @Test
    void shouldNotCreateUserWithNullEmail() {
        User user = new User();
        user.setEmail(null);
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.createUser(user));
        assertEquals("Email не может быть пустым", exception.getMessage());
    }

    @Test
    void shouldNotCreateUserWithInvalidEmail() {
        User user = new User();
        user.setEmail("invalid-email");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.createUser(user));
        assertEquals("Email должен содержать символ @", exception.getMessage());
    }

    @Test
    void shouldNotCreateUserWithEmptyLogin() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.createUser(user));
        assertEquals("Логин не может быть пустым", exception.getMessage());
    }

    @Test
    void shouldNotCreateUserWithNullLogin() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin(null);
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.createUser(user));
        assertEquals("Логин не может быть пустым", exception.getMessage());
    }

    @Test
    void shouldNotCreateUserWithLoginContainingSpaces() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("test login");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.createUser(user));
        assertEquals("Логин не может содержать пробелы", exception.getMessage());
    }

    @Test
    void shouldNotCreateUserWithFutureBirthday() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.now().plusDays(1));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.createUser(user));
        assertEquals("Дата рождения не может быть в будущем", exception.getMessage());
    }

    @Test
    void shouldNotCreateUserWithNullBirthday() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(null);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.createUser(user));
        assertEquals("Дата рождения должна быть указана", exception.getMessage());
    }

    @Test
    void shouldUpdateExistingUser() {
        // Создаем пользователя
        User user = new User();
        user.setEmail("old@example.com");
        user.setLogin("oldlogin");
        user.setName("Old Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User created = userController.createUser(user);

        // Обновляем пользователя
        created.setEmail("new@example.com");
        created.setLogin("newlogin");
        created.setName("New Name");

        User updated = userController.updateUser(created);

        assertEquals("new@example.com", updated.getEmail());
        assertEquals("newlogin", updated.getLogin());
        assertEquals("New Name", updated.getName());
        assertEquals(1, userController.getAllUsers().size());
    }

    @Test
    void shouldNotUpdateNonExistingUser() {
        User user = new User();
        user.setId(999);
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.updateUser(user));
        assertEquals("Пользователь с id 999 не найден", exception.getMessage());
    }

    @Test
    void shouldNotUpdateUserWithInvalidId() {
        User user = new User();
        user.setId(0);
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.updateUser(user));
        assertEquals("ID пользователя должен быть указан", exception.getMessage());
    }
}