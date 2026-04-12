package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Repository
@Primary
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Review> reviewRowMapper = (rs, rowNum) -> {
        Review review = new Review();
        review.setId(rs.getInt("id"));
        review.setContent(rs.getString("content"));
        review.setIsPositive(rs.getBoolean("is_positive"));
        review.setUserId(rs.getInt("user_id"));
        review.setFilmId(rs.getInt("film_id"));
        review.setUseful(rs.getInt("useful"));
        return review;
    };

    @Override
    public Review create(Review review) {
        String sql = "INSERT INTO reviews (content, is_positive, user_id, film_id, useful) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.getIsPositive());
            ps.setInt(3, review.getUserId());
            ps.setInt(4, review.getFilmId());
            ps.setInt(5, review.getUseful());
            return ps;
        }, keyHolder);
        review.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return review;
    }

    @Override
    public Review update(Review review) {
        String sql = "UPDATE reviews SET content = ?, is_positive = ? WHERE id = ?";
        int updated = jdbcTemplate.update(sql, review.getContent(), review.getIsPositive(), review.getId());
        if (updated == 0) {
            throw new NotFoundException("тзыв с id " + review.getId() + " не найден");
        }
        return findById(review.getId());
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM reviews WHERE id = ?";
        int deleted = jdbcTemplate.update(sql, id);
        if (deleted == 0) {
            throw new NotFoundException("тзыв с id " + id + " не найден");
        }
    }

    @Override
    public Review findById(int id) {
        String sql = "SELECT * FROM reviews WHERE id = ?";
        List<Review> reviews = jdbcTemplate.query(sql, reviewRowMapper, id);
        if (reviews.isEmpty()) {
            throw new NotFoundException("тзыв с id " + id + " не найден");
        }
        return reviews.get(0);
    }

    @Override
    public List<Review> findByFilmId(int filmId, int count) {
        if (filmId == 0) {
            return jdbcTemplate.query("SELECT * FROM reviews ORDER BY useful DESC LIMIT ?", reviewRowMapper, count);
        }
        return jdbcTemplate.query("SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?", reviewRowMapper, filmId, count);
    }

    @Override
    public void addLike(int reviewId, int userId) {
        jdbcTemplate.update("INSERT INTO review_likes (review_id, user_id, is_like) VALUES (?, ?, true)", reviewId, userId);
        updateUseful(reviewId);
    }

    @Override
    public void addDislike(int reviewId, int userId) {
        jdbcTemplate.update("INSERT INTO review_likes (review_id, user_id, is_like) VALUES (?, ?, false)", reviewId, userId);
        updateUseful(reviewId);
    }

    @Override
    public void removeLike(int reviewId, int userId) {
        jdbcTemplate.update("DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_like = true", reviewId, userId);
        updateUseful(reviewId);
    }

    @Override
    public void removeDislike(int reviewId, int userId) {
        jdbcTemplate.update("DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_like = false", reviewId, userId);
        updateUseful(reviewId);
    }

    private void updateUseful(int reviewId) {
        String sql = "UPDATE reviews SET useful = " +
                "(SELECT COUNT(*) FROM review_likes WHERE review_id = ? AND is_like = true) - " +
                "(SELECT COUNT(*) FROM review_likes WHERE review_id = ? AND is_like = false) " +
                "WHERE id = ?";
        jdbcTemplate.update(sql, reviewId, reviewId, reviewId);
    }
}