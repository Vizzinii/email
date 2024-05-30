package com.example.email.controller;

import com.example.email.entity.MailEntity;
import com.example.email.mailmanagement.beans.MailBean;
import com.example.email.mailmanagement.util.MailManager;
import com.example.email.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mail")
public class MailController {

    private final MailManager mailManager;

    @Autowired
    public MailController(MailManager mailManager) {
        this.mailManager = mailManager;
    }

    @PostMapping("/send")
    public String sendEmail(@RequestBody MailBean mail) {
        mailManager.sendEmail(mail);
        return "Email sent successfully";
    }

    @GetMapping("/receive")
    public List<MailBean> receiveEmails(@RequestParam String username, @RequestParam String password) {
        return mailManager.receiveEmails(username, password);
    }

    @GetMapping("/inbox")
    public List<MailEntity> getInbox(@RequestParam Long toId) {
        return MailService.getInboxByToId(toId);
    }
}