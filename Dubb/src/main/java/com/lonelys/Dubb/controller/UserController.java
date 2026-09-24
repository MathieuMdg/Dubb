package com.lonelys.Dubb.controller;

import com.lonelys.Dubb.dto.CreateUserRequest;
import com.lonelys.Dubb.dto.DtoMapper;
import com.lonelys.Dubb.dto.LoginRequest;
import com.lonelys.Dubb.dto.UserDto;
import com.lonelys.Dubb.entity.User;
import com.lonelys.Dubb.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto createUser(@RequestBody CreateUserRequest request) {
        User user = userService.createUser(request.getUsername(), request.getUserMail());
        return DtoMapper.toDto(user);
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return DtoMapper.toDto(user);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers().stream()
                .map(DtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/login")
    public UserDto login(@RequestBody LoginRequest request) {
        User user = userService.login(request.getUsername(), request.getUserMail());
        return DtoMapper.toDto(user);
    }
}