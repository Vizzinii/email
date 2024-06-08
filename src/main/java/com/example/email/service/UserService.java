package com.example.email.service;

import com.example.email.entity.UserEntity;
import com.example.email.entity.FolderEntity;
import com.example.email.repository.UserRepository;
import com.example.email.repository.FolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Value("${attachment.base-path}")
    private String attachmentBasePath;

    public String getAttachmentBasePath() {
        return attachmentBasePath;
    }

    // 使用构造器注入
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    private FolderRepository folderRepository;

    public UserEntity registerUser(String username, String email, String password) {
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(hashPassword(password));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user = userRepository.save(user);

        // 为新注册用户创建收件箱
        FolderEntity inboxFolder = new FolderEntity();
        inboxFolder.setUser(user);
        inboxFolder.setName("收件箱");
        folderRepository.save(inboxFolder);

        String attachmentFolderPath = Paths.get(attachmentBasePath, user.getEmail()).toString();
        File attachmentFolder = new File(attachmentFolderPath);
        if (!attachmentFolder.exists()) {
            attachmentFolder.mkdirs();
        }
        user.setAttachmentFolderPath(attachmentFolderPath);

        return userRepository.save(user);
    }


    public UserEntity getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // 根据用户 ID 查找用户
    public UserEntity findById(Long userId) {
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        return userOpt.orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

    public Optional<UserEntity> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<UserEntity> findUserByEmail(String Email) {
        return userRepository.findByEmail(Email);
    }

    public void deleteUser(Long userId) {
        Optional<UserEntity> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found.");
        }
        userRepository.deleteById(userId);
    }

    public String hashPassword(String password) {
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
    public UserEntity authenticateUser(String Email, String password) {
        Optional<UserEntity> userOpt = findUserByEmail(Email);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            if (validatePassword(password, user.getPassword())) {
                return user;
            }
        }
        return null;
    }
}