package com.lonelys.Dubb.service;

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

    public User createUser(String username, String userMail){
        User user = new User(username, userMail, LocalDate.now());
        User savedUser = userRepository.save(user);
        log.info("New user created : {}", savedUser);
        return savedUser;
    }
}
