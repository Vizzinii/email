package com.example.email.service;

import com.example.email.entity.MailEntity;
import com.example.email.entity.UserEntity;
import com.example.email.repository.MailRepository;
import com.example.email.repository.UserRepository;
import com.example.email.Application;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Application.class)
@TestPropertySource(locations = "classpath:application.properties")
public class TestDeleteUser {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MailRepository mailRepository;

    private Long userAId;
    private Long userBId;

    @BeforeEach
    @Transactional
    public void setUp() {
        userRepository.deleteAll();
        mailRepository.deleteAll();

        String uniqueSuffix = UUID.randomUUID().toString();

        UserEntity userA = new UserEntity();
        userA.setUsername("userA" + uniqueSuffix);
        userA.setEmail("userA@example.com");
        userA.setPassword("passwordA");
        userA.setCreatedAt(LocalDateTime.now());
        userA.setUpdatedAt(LocalDateTime.now());
        userA = userRepository.save(userA);
        userAId = userA.getUserId();

        UserEntity userB = new UserEntity();
        userB.setUsername("userB" + uniqueSuffix);
        userB.setEmail("userB@example.com");
        userB.setPassword("passwordB");
        userB.setCreatedAt(LocalDateTime.now());
        userB.setUpdatedAt(LocalDateTime.now());
        userB = userRepository.save(userB);
        userBId = userB.getUserId();

        MailEntity mail = new MailEntity();
        mail.setFromUser(userA);
        mail.setToUser(userB);
        mail.setFromEmail("userA@example.com");
        mail.setToEmail("userB@example.com");
        mail.setSubject("Test Email");
        mail.setBody("This is a test email.");
        mail.setSentDate(LocalDateTime.now());
        mail.setRead(false);
        mailRepository.save(mail);
    }

    @Test
    @Transactional
    public void testDeleteUserWithEmails() {
        userRepository.deleteById(userAId);

        List<MailEntity> emails = mailRepository.findAll();
        assertTrue(emails.isEmpty(), "Emails sent by user A should be deleted");

        assertFalse(userRepository.findById(userAId).isPresent(), "User A should be deleted");
    }
}