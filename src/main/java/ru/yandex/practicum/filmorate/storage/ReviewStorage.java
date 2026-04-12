package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;
import java.util.List;

public interface ReviewStorage {
    Review create(Review review);
    Review update(Review review);
    void delete(int id);
    Review findById(int id);
    List<Review> findByFilmId(int filmId, int count);
    void addLike(int reviewId, int userId);
    void addDislike(int reviewId, int userId);
    void removeLike(int reviewId, int userId);
    void removeDislike(int reviewId, int userId);
}