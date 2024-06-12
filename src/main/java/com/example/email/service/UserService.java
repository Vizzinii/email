package com.example.email.service;

import com.example.email.entity.AttachmentEntity;
import com.example.email.entity.UserEntity;
import com.example.email.entity.FolderEntity;
import com.example.email.entity.MailEntity;
import com.example.email.repository.MailRepository;
import com.example.email.repository.AttachmentRepository;
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
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AttachmentRepository attachmentRepository;
    private final MailRepository mailRepository;

    @Value("${attachment.base-path}")
    private String attachmentBasePath;

    public String getAttachmentBasePath() {
        return attachmentBasePath;
    }

    // 使用构造器注入
    @Autowired
    public UserService(UserRepository userRepository, AttachmentRepository attachmentRepository, MailRepository mailRepository) {
        this.attachmentRepository = attachmentRepository;
        this.userRepository = userRepository;
        this.mailRepository = mailRepository;
    }

    @Autowired
    private FolderRepository folderRepository;

    public UserEntity registerUser(String username, String email, String password) {

        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        // 检查邮箱是否已存在
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

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
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found.");
        }

        UserEntity user = userOpt.get();

        // 删除用户的所有附件记录
        List<AttachmentEntity> attachments = attachmentRepository.findByUser(user);
        for (AttachmentEntity attachment : attachments) {
            // 更新所有引用该附件的邮件，将其 attachmentId 字段设置为 NULL
            List<MailEntity> emailsWithAttachment1 = mailRepository.findByAttachment1(attachment);
            for (MailEntity email : emailsWithAttachment1) {
                email.setAttachment1(null);
                mailRepository.save(email);
            }

            List<MailEntity> emailsWithAttachment2 = mailRepository.findByAttachment2(attachment);
            for (MailEntity email : emailsWithAttachment2) {
                email.setAttachment2(null);
                mailRepository.save(email);
            }

            List<MailEntity> emailsWithAttachment3 = mailRepository.findByAttachment3(attachment);
            for (MailEntity email : emailsWithAttachment3) {
                email.setAttachment3(null);
                mailRepository.save(email);
            }

            // 删除附件文件
            File file = new File(attachment.getFilePath());
            if (file.exists()) {
                file.delete();
            }
            attachmentRepository.delete(attachment);
        }

        // 删除用户附件文件夹
        String attachmentFolderPath = user.getAttachmentFolderPath();
        if (attachmentFolderPath != null && !attachmentFolderPath.isEmpty()) {
            File attachmentFolder = new File(attachmentFolderPath);
            if (attachmentFolder.exists()) {
                deleteDirectoryRecursively(attachmentFolder);
            }
        }

        // 删除用户
        userRepository.deleteById(userId);
    }

    private void deleteDirectoryRecursively(File directory) {
        File[] allContents = directory.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectoryRecursively(file);
            }
        }
        directory.delete();
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