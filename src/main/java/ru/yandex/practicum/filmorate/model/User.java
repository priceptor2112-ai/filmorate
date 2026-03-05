package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;

public class User {
    private int id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    public int getId() {
        return id;
    }
    public String getEmail() {
        return email;
    }
    public String getLogin() {
        return login;
    }
    public String getName() {
        return name;
    }
    public LocalDate getBirthday() {
        return birthday;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setLogin(String login) {
        this.login = login;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }
}