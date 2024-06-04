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

    private final MailService mailService;

    @Autowired
    public MailController(MailManager mailManager, MailService mailService) {
        this.mailManager = mailManager;
        this.mailService = mailService;
    }

    @PostMapping("/send")
    public String sendEmail(@RequestBody MailBean mail) {
        mailManager.sendEmail(mail);
        return "Email sent successfully";
    }

    @GetMapping("/receive")
    public List<MailBean> receiveEmails(@RequestParam String username, @RequestParam String password) {
        List<MailBean> emails = mailManager.receiveEmails(username, password);

        // 获取用户 ID
        Long userId = mailService.getUserIdByUsername(username);

        // 保存接收到的邮件到数据库
        for (MailBean email : emails) {
            mailService.saveReceivedEmail(email, userId);
        }

        return emails;
    }

    @DeleteMapping("/delete")
    public String deleteEmail(@RequestParam Long emailId) {
        mailService.deleteEmail(emailId);
        return "Email deleted successfully";
    }

    @GetMapping("/inbox")
    public List<MailEntity> getInbox(@RequestParam Long toId) {
        return mailService.getInboxByToId(toId);
    }
}