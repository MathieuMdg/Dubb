package com.lonelys.Dubb.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    // Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userID;

    private String username;
    private String userMail;
    private LocalDate userJoindate;

    // OneToMany towards AttemptJPA
    // An User can have multiple Attempts
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Attempt> attempts = new ArrayList<>();


    // Constructor
    public User() {
    }

    public User(String username, String userMail, LocalDate userJoindate) {
        this.userMail = userMail;
        this.username = username;
        this.userJoindate = userJoindate;
    }

    // Getters
    public Long getUserID() {
        return userID;
    }

    public LocalDate getUserJoindate() {
        return userJoindate;
    }

    public String getUserMail() {
        return userMail;
    }

    public String getUsername() {
        return username;
    }

    public List<Attempt> getAttempts() {
        return attempts;
    }


    // Setters
    public void setAttempts(List<Attempt> attempt) {
        attempts = attempt;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public void setUserJoindate(LocalDate userJoindate) {
        this.userJoindate = userJoindate;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    // Methods
    @Override
    public String toString() {
        return username + " : {" + userMail + " | " + userJoindate + "}\n" + "Attempts -> " + attempts;
    }

}
