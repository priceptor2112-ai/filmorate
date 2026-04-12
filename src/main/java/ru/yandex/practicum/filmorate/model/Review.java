package ru.yandex.practicum.filmorate.model;

public class Review {
    private int id;
    private String content;
    private Boolean isPositive;
    private int userId;
    private int filmId;
    private int useful;

    public Review() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Boolean getIsPositive() { return isPositive; }
    public void setIsPositive(Boolean isPositive) { this.isPositive = isPositive; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getFilmId() { return filmId; }
    public void setFilmId(int filmId) { this.filmId = filmId; }
    public int getUseful() { return useful; }
    public void setUseful(int useful) { this.useful = useful; }
}