package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.EventStorage;

@Service
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final EventStorage eventStorage;

    @Autowired
    public ReviewService(ReviewStorage reviewStorage, EventStorage eventStorage) {
        this.reviewStorage = reviewStorage;
        this.eventStorage = eventStorage;
    }

    public Review create(Review review) {
        Review created = reviewStorage.create(review);
        eventStorage.addEvent(review.getUserId(), "REVIEW", "ADD", created.getId());
        return created;
    }

    public Review update(Review review) {
        Review old = reviewStorage.findById(review.getId());
        Review updated = reviewStorage.update(review);
        eventStorage.addEvent(old.getUserId(), "REVIEW", "UPDATE", review.getId());
        return updated;
    }

    public void delete(int id) {
        Review review = reviewStorage.findById(id);
        reviewStorage.delete(id);
        eventStorage.addEvent(review.getUserId(), "REVIEW", "REMOVE", id);
    }

    public Review findById(int id) { return reviewStorage.findById(id); }
    public java.util.List<Review> findByFilmId(int filmId, int count) { return reviewStorage.findByFilmId(filmId, count); }
    public void addLike(int reviewId, int userId) { reviewStorage.addLike(reviewId, userId); eventStorage.addEvent(userId, "LIKE", "ADD", reviewId); }
    public void addDislike(int reviewId, int userId) { reviewStorage.addDislike(reviewId, userId); eventStorage.addEvent(userId, "LIKE", "ADD", reviewId); }
    public void removeLike(int reviewId, int userId) { reviewStorage.removeLike(reviewId, userId); eventStorage.addEvent(userId, "LIKE", "REMOVE", reviewId); }
    public void removeDislike(int reviewId, int userId) { reviewStorage.removeDislike(reviewId, userId); eventStorage.addEvent(userId, "LIKE", "REMOVE", reviewId); }
}