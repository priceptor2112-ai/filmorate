package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventStorage;

import java.util.List;

@Service
public class FeedService {
    private final EventStorage eventStorage;

    @Autowired
    public FeedService(EventStorage eventStorage) {
        this.eventStorage = eventStorage;
    }

    public List<Event> getUserFeed(int userId) {
        return eventStorage.getUserFeed(userId);
    }
}