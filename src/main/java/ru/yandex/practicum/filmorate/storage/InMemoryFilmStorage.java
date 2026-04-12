package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int currentId = 1;

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film findById(int id) {
        Film film = films.get(id);
        if (film == null) {
            throw new NotFoundException(String.format("ильм с id %d не найден", id));
        }
        return film;
    }

    @Override
    public Film create(Film film) {
        film.setId(currentId++);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException(String.format("ильм с id %d не найден", film.getId()));
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void delete(int id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException(String.format("ильм с id %d не найден", id));
        }
        films.remove(id);
    }

    @Override
    public boolean existsById(int id) {
        return films.containsKey(id);
    }

    @Override
    public void addLike(int filmId, int userId) {
        Film film = findById(filmId);
        film.addLike(userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        Film film = findById(filmId);
        film.removeLike(userId);
    }

    @Override
    public List<Film> getMostLikedFilms(int count) {
        return findAll().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikesCount(), f1.getLikesCount()))
                .limit(count)
                .collect(Collectors.toList());
    }
}