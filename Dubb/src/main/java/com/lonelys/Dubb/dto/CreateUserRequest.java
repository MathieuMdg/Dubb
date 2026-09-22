package com.lonelys.Dubb.dto;

public class CreateUserRequest {

    private String username;
    private String userMail;

    public CreateUserRequest() {
    }

    public CreateUserRequest(String username, String userMail) {
        this.username = username;
        this.userMail = userMail;
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
}