package com.example.email.controller;

import com.example.email.entity.UserEntity;
import com.example.email.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // 使用构造器注入
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public UserEntity registerUser(@RequestParam String username,
                                   @RequestParam String email,
                                   @RequestParam String password) {
        return userService.registerUser(username, email, password);
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String username, @RequestParam String password) {
        UserEntity user = userService.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found."));

        if (!userService.validatePassword(password, user.getPassword())) {
            throw new RuntimeException("Invalid password.");
        }

        return "Login successful";
    }

    @DeleteMapping("/delete")
    public void deleteUser(@RequestParam Long userId) {
        userService.deleteUser(userId);
    }
}