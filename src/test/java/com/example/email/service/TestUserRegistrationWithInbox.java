package com.example.email.service;

import com.example.email.entity.FolderEntity;
import com.example.email.entity.UserEntity;
import com.example.email.repository.FolderRepository;
import com.example.email.repository.UserRepository;
import com.example.email.Application;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Application.class)
@TestPropertySource(locations = "classpath:application.properties")
public class TestUserRegistrationWithInbox {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FolderRepository folderRepository;

    private String uniqueSuffix;

    @BeforeEach
    @Transactional
    public void setUp() {
        userRepository.deleteAll();
        folderRepository.deleteAll();
        uniqueSuffix = UUID.randomUUID().toString();
    }

    @Test
    @Transactional
    public void testRegisterUserAAndCheckInbox() {
        // 注册用户A
        UserEntity userA = new UserEntity();
        userA.setUsername("userA" + uniqueSuffix);
        userA.setEmail("userA@example.com");
        userA.setPassword("passwordA");
        userA.setCreatedAt(LocalDateTime.now());
        userA.setUpdatedAt(LocalDateTime.now());
        userA = userRepository.save(userA);

        // 检查用户A是否成功注册
        Optional<UserEntity> foundUserOpt = userRepository.findByUsername("userA" + uniqueSuffix);
        assertTrue(foundUserOpt.isPresent(), "User A should be found in the database");

        UserEntity foundUser = foundUserOpt.get();
        assertEquals("userA" + uniqueSuffix, foundUser.getUsername());
        assertEquals("userA@example.com", foundUser.getEmail());
        assertEquals("passwordA", foundUser.getPassword());

        // 检查是否为用户A创建了收件箱
        Optional<FolderEntity> inboxFolderOpt = folderRepository.findByUserUserIdAndName(foundUser.getUserId(), "收件箱");
        assertTrue(inboxFolderOpt.isPresent(), "Inbox folder should be created for user A");

        FolderEntity inboxFolder = inboxFolderOpt.get();
        assertEquals("收件箱", inboxFolder.getName());
        assertEquals(foundUser.getUserId(), inboxFolder.getUser().getUserId());
    }
}