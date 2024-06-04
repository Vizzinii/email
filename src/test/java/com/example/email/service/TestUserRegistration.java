package com.example.email.service;

import com.example.email.entity.UserEntity;
import com.example.email.repository.UserRepository;
import com.example.email.Application;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.Commit;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Application.class)
@TestPropertySource(locations = "classpath:application.properties")
public class TestUserRegistration {

    @Autowired
    private UserRepository userRepository;

    private String uniqueSuffix;

    @BeforeEach
    @Transactional
    @Commit // 或 @Rollback(false)
    public void setUp() {
        userRepository.deleteAll();
        uniqueSuffix = UUID.randomUUID().toString();
    }

    @Test
    @Transactional
    @Commit // 或 @Rollback(false)
    public void testRegisterUser() {
        UserEntity user = new UserEntity();
        user.setUsername("userC" + uniqueSuffix);
        user.setEmail("userC@example.com");
        user.setPassword("passwordC");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        Optional<UserEntity> foundUser = userRepository.findByUsername("userC" + uniqueSuffix);
        assertTrue(foundUser.isPresent(), "User C should be found in the database");

        UserEntity savedUser = foundUser.get();
        assertEquals("userC" + uniqueSuffix, savedUser.getUsername());
        assertEquals("userC@example.com", savedUser.getEmail());
        assertEquals("passwordC", savedUser.getPassword());
    }

}