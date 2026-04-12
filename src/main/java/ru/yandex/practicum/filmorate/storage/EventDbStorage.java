package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

@Repository
public class EventDbStorage implements EventStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public EventDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Event> eventRowMapper = (rs, rowNum) -> {
        Event event = new Event();
        event.setEventId(rs.getInt("event_id"));
        event.setUserId(rs.getInt("user_id"));
        event.setTimestamp(rs.getLong("timestamp"));
        event.setEventType(rs.getString("event_type"));
        event.setOperation(rs.getString("operation"));
        event.setEntityId(rs.getInt("entity_id"));
        return event;
    };

    @Override
    public void addEvent(int userId, String eventType, String operation, int entityId) {
        String sql = "INSERT INTO events (user_id, timestamp, event_type, operation, entity_id) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, userId, System.currentTimeMillis(), eventType, operation, entityId);
    }

    @Override
    public List<Event> getUserFeed(int userId) {
        String sql = "SELECT * FROM events WHERE user_id = ? ORDER BY timestamp ASC";
        List<Event> events = jdbcTemplate.query(sql, eventRowMapper, userId);
        if (events.isEmpty()) {
            throw new NotFoundException("События для пользователя с id " + userId + " не найдены");
        }
        return events;
    }
}