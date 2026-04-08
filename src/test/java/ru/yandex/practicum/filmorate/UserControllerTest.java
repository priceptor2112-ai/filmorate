package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private UserController userController;
    private InMemoryUserStorage userStorage;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
        userController = new UserController(userStorage, userService);
    }

    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================

    private User createTestUser(String email, String login, String name, LocalDate birthday) {
        User user = new User();
        user.setEmail(email);
        user.setLogin(login);
        user.setName(name);
        user.setBirthday(birthday);
        return user;
    }

    private User createAndSaveUser(String email, String login, String name, LocalDate birthday) {
        User user = createTestUser(email, login, name, birthday);
        return userController.createUser(user);
    }

    // ==================== ТЕСТЫ СОЗДАНИЯ ПОЛЬЗОВАТЕЛЯ ====================

    @Test
    void shouldCreateValidUser() {
        User user = createTestUser("test@example.com", "testlogin", "Test User", LocalDate.of(1990, 1, 1));

        User created = userController.createUser(user);

        assertNotNull(created.getId());
        assertEquals(1, created.getId());
        assertEquals("test@example.com", created.getEmail());
        assertEquals("testlogin", created.getLogin());
        assertEquals("Test User", created.getName());
        assertEquals(LocalDate.of(1990, 1, 1), created.getBirthday());
        assertNotNull(created.getFriends());
        assertEquals(0, created.getFriends().size());
    }

    @Test
    void shouldSetNameAsLoginWhenNameIsEmpty() {
        User user = createTestUser("test@example.com", "testlogin", "", LocalDate.of(1990, 1, 1));

        User created = userController.createUser(user);
        assertEquals("testlogin", created.getName());
    }

    @Test
    void shouldSetNameAsLoginWhenNameIsNull() {
        User user = createTestUser("test@example.com", "testlogin", null, LocalDate.of(1990, 1, 1));

        User created = userController.createUser(user);
        assertEquals("testlogin", created.getName());
    }

    @Test
    void shouldSetNameAsLoginWhenNameIsBlank() {
        User user = createTestUser("test@example.com", "testlogin", "   ", LocalDate.of(1990, 1, 1));

        User created = userController.createUser(user);
        assertEquals("testlogin", created.getName());
    }

    @Test
    void shouldNotCreateUserWithEmptyEmail() {
        User user = createTestUser("", "testlogin", "Test User", LocalDate.of(1990, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.createUser(user));
        assertEquals("Email не может быть пустым", exception.getMessage());
    }

    @Test
    void shouldNotCreateUserWithNullEmail() {
        User user = createTestUser(null, "testlogin", "Test User", LocalDate.of(1990, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.createUser(user));
        assertEquals("Email не может быть пустым", exception.getMessage());
    }

    @Test
    void shouldNotCreateUserWithInvalidEmailNoAtSymbol() {
        User user = createTestUser("invalid-email", "testlogin", "Test User", LocalDate.of(1990, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.createUser(user));
        assertEquals("Email должен содержать символ @", exception.getMessage());
    }

    @Test
    void shouldNotCreateUserWithEmailOnlyAtSymbol() {
        User user = createTestUser("@", "testlogin", "Test User", LocalDate.of(1990, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.createUser(user));
        assertEquals("Email должен содержать символ @", exception.getMessage());
    }

    @Test
    void shouldNotCreateUserWithEmptyLogin() {
        User user = createTestUser("test@example.com", "", "Test User", LocalDate.of(1990, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.createUser(user));
        assertEquals("Логин не может быть пустым", exception.getMessage());
    }

    @Test
    void shouldNotCreateUserWithNullLogin() {
        User user = createTestUser("test@example.com", null, "Test User", LocalDate.of(1990, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.createUser(user));
        assertEquals("Логин не может быть пустым", exception.getMessage());
    }

    @Test
    void shouldNotCreateUserWithLoginContainingSpaces() {
        User user = createTestUser("test@example.com", "test login", "Test User", LocalDate.of(1990, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.createUser(user));
        assertEquals("Логин не может содержать пробелы", exception.getMessage());
    }

    @Test
    void shouldNotCreateUserWithLoginHavingLeadingSpace() {
        User user = createTestUser("test@example.com", " testlogin", "Test User", LocalDate.of(1990, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.createUser(user));
        assertEquals("Логин не может содержать пробелы", exception.getMessage());
    }

    @Test
    void shouldNotCreateUserWithFutureBirthday() {
        User user = createTestUser("test@example.com", "testlogin", "Test User", LocalDate.now().plusDays(1));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.createUser(user));
        assertEquals("Дата рождения не может быть в будущем", exception.getMessage());
    }

    @Test
    void shouldNotCreateUserWithNullBirthday() {
        User user = createTestUser("test@example.com", "testlogin", "Test User", null);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.createUser(user));
        assertEquals("Дата рождения должна быть указана", exception.getMessage());
    }

    @Test
    void shouldCreateUserWithBirthdayToday() {
        User user = createTestUser("test@example.com", "testlogin", "Test User", LocalDate.now());

        User created = userController.createUser(user);
        assertEquals(LocalDate.now(), created.getBirthday());
    }

    // ==================== ТЕСТЫ ОБНОВЛЕНИЯ ПОЛЬЗОВАТЕЛЯ ====================

    @Test
    void shouldUpdateExistingUser() {
        User created = createAndSaveUser("old@example.com", "oldlogin", "Old Name", LocalDate.of(1990, 1, 1));

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
    void shouldUpdateUserWithEmptyNameUseLogin() {
        User created = createAndSaveUser("test@example.com", "testlogin", "Old Name", LocalDate.of(1990, 1, 1));

        created.setName("");

        User updated = userController.updateUser(created);
        assertEquals("testlogin", updated.getName());
    }

    @Test
    void shouldNotUpdateNonExistingUser() {
        User user = createTestUser("test@example.com", "testlogin", "Test User", LocalDate.of(1990, 1, 1));
        user.setId(999);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userController.updateUser(user));
        assertEquals("Пользователь с id 999 не найден", exception.getMessage());
    }

    @Test
    void shouldNotUpdateUserWithInvalidId() {
        User user = createTestUser("test@example.com", "testlogin", "Test User", LocalDate.of(1990, 1, 1));
        user.setId(0);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.updateUser(user));
        assertEquals("ID пользователя должен быть указан", exception.getMessage());
    }

    @Test
    void shouldNotUpdateUserWithNegativeId() {
        User user = createTestUser("test@example.com", "testlogin", "Test User", LocalDate.of(1990, 1, 1));
        user.setId(-1);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.updateUser(user));
        assertEquals("ID пользователя должен быть указан", exception.getMessage());
    }

    // ==================== ТЕСТЫ ПОЛУЧЕНИЯ ПОЛЬЗОВАТЕЛЕЙ ====================

    @Test
    void shouldGetAllUsers() {
        createAndSaveUser("user1@example.com", "user1", "User One", LocalDate.of(1990, 1, 1));
        createAndSaveUser("user2@example.com", "user2", "User Two", LocalDate.of(1991, 2, 2));

        List<User> allUsers = userController.getAllUsers();

        assertEquals(2, allUsers.size());
    }

    @Test
    void shouldGetEmptyListWhenNoUsers() {
        List<User> allUsers = userController.getAllUsers();
        assertEquals(0, allUsers.size());
    }

    @Test
    void shouldGetUserById() {
        User created = createAndSaveUser("test@example.com", "testlogin", "Test User", LocalDate.of(1990, 1, 1));

        User found = userController.getUserById(created.getId());

        assertEquals(created.getId(), found.getId());
        assertEquals("test@example.com", found.getEmail());
        assertEquals("testlogin", found.getLogin());
    }

    @Test
    void shouldThrowNotFoundWhenGettingNonExistingUser() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userController.getUserById(999));
        assertEquals("Пользователь с id 999 не найден", exception.getMessage());
    }

    // ==================== ТЕСТЫ ДРУЗЕЙ ====================

    @Test
    void shouldAddFriend() {
        User user1 = createAndSaveUser("user1@example.com", "user1", "User One", LocalDate.of(1990, 1, 1));
        User user2 = createAndSaveUser("user2@example.com", "user2", "User Two", LocalDate.of(1991, 2, 2));

        userController.addFriend(user1.getId(), user2.getId());

        List<User> friendsOfUser1 = userController.getFriends(user1.getId());
        List<User> friendsOfUser2 = userController.getFriends(user2.getId());

        assertEquals(1, friendsOfUser1.size());
        assertEquals(1, friendsOfUser2.size());
        assertEquals(user2.getId(), friendsOfUser1.get(0).getId());
        assertEquals(user1.getId(), friendsOfUser2.get(0).getId());
    }

    @Test
    void shouldNotAddSelfAsFriend() {
        User user = createAndSaveUser("user@example.com", "user", "User", LocalDate.of(1990, 1, 1));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userController.addFriend(user.getId(), user.getId()));
        assertEquals("Нельзя добавить самого себя в друзья", exception.getMessage());
    }

    @Test
    void shouldNotAddFriendWhenUserNotFound() {
        User user = createAndSaveUser("user@example.com", "user", "User", LocalDate.of(1990, 1, 1));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userController.addFriend(user.getId(), 999));
        assertEquals("Пользователь с id 999 не найден", exception.getMessage());
    }

    @Test
    void shouldNotAddFriendWhenFriendNotFound() {
        User user = createAndSaveUser("user@example.com", "user", "User", LocalDate.of(1990, 1, 1));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userController.addFriend(999, user.getId()));
        assertEquals("Пользователь с id 999 не найден", exception.getMessage());
    }

    @Test
    void shouldRemoveFriend() {
        User user1 = createAndSaveUser("user1@example.com", "user1", "User One", LocalDate.of(1990, 1, 1));
        User user2 = createAndSaveUser("user2@example.com", "user2", "User Two", LocalDate.of(1991, 2, 2));

        userController.addFriend(user1.getId(), user2.getId());
        userController.removeFriend(user1.getId(), user2.getId());

        List<User> friendsOfUser1 = userController.getFriends(user1.getId());
        List<User> friendsOfUser2 = userController.getFriends(user2.getId());

        assertEquals(0, friendsOfUser1.size());
        assertEquals(0, friendsOfUser2.size());
    }

    @Test
    void shouldGetFriendsList() {
        User user1 = createAndSaveUser("user1@example.com", "user1", "User One", LocalDate.of(1990, 1, 1));
        User user2 = createAndSaveUser("user2@example.com", "user2", "User Two", LocalDate.of(1991, 2, 2));
        User user3 = createAndSaveUser("user3@example.com", "user3", "User Three", LocalDate.of(1992, 3, 3));

        userController.addFriend(user1.getId(), user2.getId());
        userController.addFriend(user1.getId(), user3.getId());

        List<User> friends = userController.getFriends(user1.getId());

        assertEquals(2, friends.size());
        assertTrue(friends.stream().anyMatch(u -> u.getId() == user2.getId()));
        assertTrue(friends.stream().anyMatch(u -> u.getId() == user3.getId()));
    }

    @Test
    void shouldGetEmptyFriendsListWhenNoFriends() {
        User user = createAndSaveUser("user@example.com", "user", "User", LocalDate.of(1990, 1, 1));

        List<User> friends = userController.getFriends(user.getId());

        assertEquals(0, friends.size());
    }

    // ==================== ТЕСТЫ ОБЩИХ ДРУЗЕЙ ====================

    @Test
    void shouldGetCommonFriends() {
        User user1 = createAndSaveUser("user1@example.com", "user1", "User One", LocalDate.of(1990, 1, 1));
        User user2 = createAndSaveUser("user2@example.com", "user2", "User Two", LocalDate.of(1991, 2, 2));
        User commonFriend = createAndSaveUser("common@example.com", "common", "Common Friend", LocalDate.of(1992, 3, 3));
        User notCommonFriend = createAndSaveUser("notcommon@example.com", "notcommon", "Not Common", LocalDate.of(1993, 4, 4));

        userController.addFriend(user1.getId(), commonFriend.getId());
        userController.addFriend(user2.getId(), commonFriend.getId());
        userController.addFriend(user1.getId(), notCommonFriend.getId());

        List<User> commonFriends = userController.getCommonFriends(user1.getId(), user2.getId());

        assertEquals(1, commonFriends.size());
        assertEquals(commonFriend.getId(), commonFriends.get(0).getId());
    }

    @Test
    void shouldReturnEmptyListWhenNoCommonFriends() {
        User user1 = createAndSaveUser("user1@example.com", "user1", "User One", LocalDate.of(1990, 1, 1));
        User user2 = createAndSaveUser("user2@example.com", "user2", "User Two", LocalDate.of(1991, 2, 2));
        User friend1 = createAndSaveUser("friend1@example.com", "friend1", "Friend One", LocalDate.of(1992, 3, 3));
        User friend2 = createAndSaveUser("friend2@example.com", "friend2", "Friend Two", LocalDate.of(1993, 4, 4));

        userController.addFriend(user1.getId(), friend1.getId());
        userController.addFriend(user2.getId(), friend2.getId());

        List<User> commonFriends = userController.getCommonFriends(user1.getId(), user2.getId());

        assertEquals(0, commonFriends.size());
    }
}