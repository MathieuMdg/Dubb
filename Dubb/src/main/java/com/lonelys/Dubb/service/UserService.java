package com.lonelys.Dubb.service;

import com.lonelys.Dubb.exception.DuplicateException;
import com.lonelys.Dubb.exception.InvalidUserDataException;
import com.lonelys.Dubb.repository.UserRepository;
import com.lonelys.Dubb.entity.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class UserService {

    // Attributes
    // Specify UserService in logs
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    // Service access UserRepository
    private final UserRepository userRepository;

    // Constructor
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    // Methods
    // Create a user and add it to the database
    public User createUser(String username, String userMail){
        checkUserData(username, userMail);
        checkAvailability(username, userMail);
        User user = new User(username, userMail, LocalDate.now());
        User savedUser = userRepository.save(user);
        log.info("New user created : {}", savedUser);
        return savedUser;
    }

    // Check if the data given are valid to create a user
    private void checkUserData(String username, String userMail){
        if(username == null || username.isBlank()){
            log.warn("Attempt to create with an invalid username");
            throw new InvalidUserDataException("Username invalid");
        }
        if(userMail == null || userMail.isBlank()){
            log.warn("Attempt to create with an invalid Mail");
            throw new InvalidUserDataException("Mail invalid");
        }
    }

    // Check if there's no duplicate data
    private void checkAvailability(String username, String userMail){

        if(userRepository.existsByUserMail(userMail)) {
            log.warn("Attempt to create with an duplicate Mail");
            throw new DuplicateException("Mail already used by another account : " + userMail);
        }

        if(userRepository.existsByUsername(username)) {
            log.warn("Attempt to create with an duplicate Username");
            throw new DuplicateException("Username already used by another account : " + username);
        }
    }
}
