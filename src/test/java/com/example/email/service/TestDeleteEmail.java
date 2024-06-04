package com.example.email.service;

import com.example.email.entity.MailEntity;
import com.example.email.repository.MailRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
public class TestDeleteEmail {

    @Autowired
    private MailService mailService;

    @Autowired
    private MailRepository mailRepository;

    private Long testEmailId;



    @Test
    @Transactional
    public void testDeleteEmail() {
        long testEmailId = 11;
        mailService.deleteEmail(testEmailId);

        Optional<MailEntity> deletedMail = mailRepository.findById(testEmailId);
        assertTrue(deletedMail.isEmpty(), "Email should be deleted");
    }
}