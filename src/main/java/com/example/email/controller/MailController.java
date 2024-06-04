package com.example.email.controller;

import com.example.email.entity.MailEntity;
import com.example.email.entity.UserEntity;
import com.example.email.mailmanagement.beans.MailBean;
import com.example.email.mailmanagement.util.MailManager;
import com.example.email.service.MailService;
import com.example.email.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/mail")
public class MailController {

    private static final Logger logger = LoggerFactory.getLogger(MailController.class);

    private final MailManager mailManager;
    private final MailService mailService;
    private final UserService userService;

    @Autowired
    public MailController(MailManager mailManager, MailService mailService, UserService userService) {
        this.mailManager = mailManager;
        this.mailService = mailService;
        this.userService = userService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody MailBean mail) {
        try {
            UserEntity fromUser = userService.findUserByEmail(mail.getFrom())
                    .orElseThrow(() -> new RuntimeException("Sender not found"));
            UserEntity toUser = userService.findUserByEmail(mail.getTo())
                    .orElseThrow(() -> new RuntimeException("Recipient not found"));

            MailEntity mailEntity = new MailEntity();
            mailEntity.setFromUser(fromUser);
            mailEntity.setToUser(toUser);
            mailEntity.setFromEmail(mail.getFrom());
            mailEntity.setToEmail(mail.getTo());
            mailEntity.setSubject(mail.getSubject());
            mailEntity.setBody(mail.getBody());
            mailEntity.setSentDate(LocalDateTime.now());
            mailEntity.setRead(false);

            mailService.saveMail(mailEntity);
            mailManager.sendEmail(mail); // 假设这是实际发送邮件的逻辑

            return ResponseEntity.ok("Email sent successfully");
        } catch (Exception e) {
            logger.error("Error sending email: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email");
        }
    }

    @GetMapping("/receive")
    public List<MailBean> receiveEmails(@RequestParam String username, @RequestParam String password) {
        List<MailBean> emails = mailManager.receiveEmails(username, password);

        Long userId = mailService.getUserIdByUsername(username);

        for (MailBean email : emails) {
            mailService.saveReceivedEmail(email, userId);
        }

        return emails;
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteEmail(@RequestParam Long emailId) {
        try {
            mailService.deleteEmail(emailId);
            return ResponseEntity.ok("Email deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete email");
        }
    }

    @GetMapping("/inbox")
    public ResponseEntity<List<MailEntity>> getInbox(@RequestParam Long toId) {
        try {
            List<MailEntity> inbox = mailService.getInboxByToId(toId);
            return ResponseEntity.ok(inbox);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}