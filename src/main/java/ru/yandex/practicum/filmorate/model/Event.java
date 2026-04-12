package ru.yandex.practicum.filmorate.model;

public class Event {
    private int eventId;
    private int userId;
    private long timestamp;
    private String eventType;
    private String operation;
    private int entityId;

    public Event() {}

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }
    public int getEntityId() { return entityId; }
    public void setEntityId(int entityId) { this.entityId = entityId; }
}