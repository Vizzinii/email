package com.example.email.controller;

import com.example.email.entity.UserEntity;
import com.example.email.service.UserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    // 使用构造器注入
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserEntity> registerUser(@RequestBody UserEntity user) {
        UserEntity registeredUser = userService.registerUser(user.getUsername(), user.getEmail(), user.getPassword());
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserEntity loginRequest) {
        logger.info("Login attempt with email: {}", loginRequest.getEmail());
        UserEntity user = userService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());
        if (user != null) {
            logger.info("Login successful for email: {}", loginRequest.getEmail());
            return ResponseEntity.ok(user);
        } else {
            logger.warn("Login failed for email: {}", loginRequest.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password.");
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUser(@RequestParam Long userId) {
        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("API is working");
    }

    @GetMapping("/getUserIdByEmail")
    public ResponseEntity<UserIdResponse> getUserIdByEmail(@RequestParam String email) {
        try {
            UserEntity user = userService.findUserByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            UserIdResponse response = new UserIdResponse(user.getUserId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public class UserIdResponse {
        private Long userId;

        public UserIdResponse(Long userId) {
            this.userId = userId;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }
    }
}