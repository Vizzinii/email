package com.example.email.service;

import com.example.email.entity.MailEntity;
import com.example.email.mailmanagement.beans.MailBean;
import com.example.email.mailmanagement.util.MailManager;
import com.example.email.repository.MailRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class MailServiceTest {

    @Autowired
    private MailManager mailManager;

    @Autowired
    private MailRepository mailRepository;

    @Test
    public void testSendEmail() {
        MailBean mail = new MailBean();
        mail.setFrom("userE@example.com");
        mail.setTo("userF@example.com");
        mail.setSubject("Test Email");
        mail.setBody("This is a test email.");
        mail.setSentDate(LocalDateTime.now());
        mail.setAttachments(Collections.emptyList());

        mailManager.sendEmail(mail);

        // 检查邮件是否已存入数据库
        List<MailEntity> emails = mailRepository.findAll();
        boolean mailFound = emails.stream().anyMatch(e -> e.getFromEmail().equals("userE@example.com") &&
                e.getToEmail().equals("userF@example.com") &&
                e.getSubject().equals("Test Email") &&
                e.getBody().equals("This is a test email."));
        assertTrue(mailFound, "Email not found in database.");
    }

    @Test
    public void testReceiveEmails() {
        String username = "pop3User"; // 确保此处的用户名和密码与POP3配置匹配
        String password = "pop3Password";

        List<MailBean> receivedEmails = mailManager.receiveEmails(username, password);
        assertFalse(receivedEmails.isEmpty(), "No emails received.");
    }
}