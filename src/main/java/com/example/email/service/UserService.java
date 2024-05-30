package com.example.email.service;

import com.example.email.entity.UserEntity;
import com.example.email.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    // 使用构造器注入
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEntity registerUser(String username, String email, String password) {
        if (userRepository.findByUsername(username) != null || userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Username or email already exists.");
        }

        String hashedPassword = hashPassword(password);
        UserEntity newUser = new UserEntity();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(hashedPassword);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(newUser);
    }

    public Optional<UserEntity> findUserByUsername(String username) {
        return Optional.ofNullable(userRepository.findByUsername(username));
    }

    public void deleteUser(Long userId) {
        Optional<UserEntity> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found.");
        }
        userRepository.deleteById(userId);
    }

    String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public boolean validatePassword(String rawPassword, String storedHash) {
        return storedHash.equals(hashPassword(rawPassword));
    }
    public UserEntity authenticateUser(String username, String password) {
        Optional<UserEntity> user = findUserByUsername(username);
        if (user.isPresent() && validatePassword(password, user.get().getPassword())) {
            return user.get();
        }
        return null;
    }
}