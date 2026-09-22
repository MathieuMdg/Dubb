package com.lonelys.Dubb.dto;

import java.time.LocalDate;

public class UserDto {

    private Long userId;
    private String username;
    private String userMail;
    private LocalDate userJoindate;

    public UserDto() {
    }

    public UserDto(Long userId, String username, String userMail, LocalDate userJoindate) {
        this.userId = userId;
        this.username = username;
        this.userMail = userMail;
        this.userJoindate = userJoindate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public LocalDate getUserJoindate() {
        return userJoindate;
    }

    public void setUserJoindate(LocalDate userJoindate) {
        this.userJoindate = userJoindate;
    }
}