package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FilmorateApplicationTests {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	void contextLoads() {
		// Проверяем, что контекст Spring загрузился
		assertThat(applicationContext).isNotNull();
	}

	@Test
	void testFilmControllerBeanExists() {
		FilmController filmController = applicationContext.getBean(FilmController.class);
		assertThat(filmController).isNotNull();
	}

	@Test
	void testUserControllerBeanExists() {
		UserController userController = applicationContext.getBean(UserController.class);
		assertThat(userController).isNotNull();
	}

	@Test
	void testFilmServiceBeanExists() {
		FilmService filmService = applicationContext.getBean(FilmService.class);
		assertThat(filmService).isNotNull();
	}

	@Test
	void testUserServiceBeanExists() {
		UserService userService = applicationContext.getBean(UserService.class);
		assertThat(userService).isNotNull();
	}

	@Test
	void testFilmStorageBeanExists() {
		FilmStorage filmStorage = applicationContext.getBean(FilmStorage.class);
		assertThat(filmStorage).isNotNull();
	}

	@Test
	void testUserStorageBeanExists() {
		UserStorage userStorage = applicationContext.getBean(UserStorage.class);
		assertThat(userStorage).isNotNull();
	}

	@Test
	void testAllBeansAreCorrectTypes() {
		// Проверяем, что бины имеют правильные типы (интерфейсы)
		FilmStorage filmStorage = applicationContext.getBean(FilmStorage.class);
		UserStorage userStorage = applicationContext.getBean(UserStorage.class);

		// Проверяем, что реализации являются InMemory (на данный момент)
		assertThat(filmStorage.getClass().getSimpleName()).isEqualTo("InMemoryFilmStorage");
		assertThat(userStorage.getClass().getSimpleName()).isEqualTo("InMemoryUserStorage");
	}

	@Test
	void testDependencyInjectionWorks() {
		// Проверяем, что зависимости внедрены корректно
		FilmController filmController = applicationContext.getBean(FilmController.class);
		UserController userController = applicationContext.getBean(UserController.class);

		assertThat(filmController).isNotNull();
		assertThat(userController).isNotNull();
	}
}