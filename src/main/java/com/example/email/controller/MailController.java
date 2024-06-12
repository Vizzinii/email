package com.example.email.controller;

import com.example.email.entity.AttachmentEntity;
import com.example.email.entity.MailEntity;
import com.example.email.entity.UserEntity;
import com.example.email.entity.FolderEntity;
import com.example.email.mailmanagement.beans.MailBean;
import com.example.email.mailmanagement.util.MailManager;
import com.example.email.repository.AttachmentRepository;
import com.example.email.service.FolderService;
import com.example.email.service.MailService;
import com.example.email.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/mail")
@CrossOrigin(origins = "http://localhost:3000")
// 前端应用的 URL
public class MailController {

    private static final Logger logger = LoggerFactory.getLogger(MailController.class);

    private final MailManager mailManager;
    private final MailService mailService;
    private final UserService userService;
    private final FolderService folderService;
    private final AttachmentRepository attachmentRepository;


    @Autowired
    public MailController(MailManager mailManager, MailService mailService, UserService userService, FolderService folderService, AttachmentRepository attachmentRepository) {
        this.mailManager = mailManager;
        this.mailService = mailService;
        this.userService = userService;
        this.folderService= folderService;
        this.attachmentRepository=attachmentRepository;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody MailBean mail) {
        logger.info("Received send email request: {}", mail);
        try {
            UserEntity fromUser = userService.findUserByEmail(mail.getFrom())
                    .orElseThrow(() -> new RuntimeException("Sender not found"));
            UserEntity toUser = userService.findUserByEmail(mail.getTo())
                    .orElseThrow(() -> new RuntimeException("Recipient not found"));

            // 将 MailBean 转换为 MailEntity
            MailEntity mailEntity = new MailEntity();
            mailEntity.setFromUser(fromUser);
            mailEntity.setToUser(toUser);
            mailEntity.setFromEmail(mail.getFrom());
            mailEntity.setToEmail(mail.getTo());
            mailEntity.setSubject(mail.getSubject());
            mailEntity.setBody(mail.getBody());
            mailEntity.setSentDate(LocalDateTime.now());
            mailEntity.setRead(false);

            // 处理附件
            if (mail.getAttachments() != null && !mail.getAttachments().isEmpty()) {
                if (mail.getAttachments().size() > 0) {
                    Long attachmentId1 = mail.getAttachments().get(0).getId();
                    AttachmentEntity attachment1 = attachmentRepository.findById(attachmentId1)
                            .orElseThrow(() -> new RuntimeException("Attachment not found"));
                    mailEntity.setAttachment1(attachment1);
                }
                if (mail.getAttachments().size() > 1) {
                    Long attachmentId2 = mail.getAttachments().get(1).getId();
                    AttachmentEntity attachment2 = attachmentRepository.findById(attachmentId2)
                            .orElseThrow(() -> new RuntimeException("Attachment not found"));
                    mailEntity.setAttachment2(attachment2);
                }
                if (mail.getAttachments().size() > 2) {
                    Long attachmentId3 = mail.getAttachments().get(2).getId();
                    AttachmentEntity attachment3 = attachmentRepository.findById(attachmentId3)
                            .orElseThrow(() -> new RuntimeException("Attachment not found"));
                    mailEntity.setAttachment3(attachment3);
                }
            }

            // 查找收件人的收件箱文件夹并设置
            Long toUserId = toUser.getUserId();
            Optional<FolderEntity> inboxFolder = folderService.findInboxFolderByUserId(toUserId);
            if (inboxFolder.isPresent()) {
                mailEntity.setFolder(inboxFolder.get());
            } else {
                logger.error("Recipient inbox folder not found for userId: " + toUserId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Recipient inbox folder not found");
            }

            mailService.saveMail(mailEntity); // 保存邮件记录

            //mailManager.sendEmail(mail); // 调用发送邮件的方法，只能发送简单邮件

            logger.info("Email sent successfully from {} to {}", mail.getFrom(), mail.getTo());
            return ResponseEntity.ok("Email sent successfully");
        } catch (Exception e) {
            logger.error("Error sending email: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email");
        }
    }

    @GetMapping("/receive")
    public List<MailBean> receiveEmails(@RequestParam String username, @RequestParam String password) {
        logger.info("Received receive emails request for username: {}", username);
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
            logger.info("Received delete email request for emailId: {}", emailId);
            mailService.deleteEmail(emailId);
            return ResponseEntity.ok("Email deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting email: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete email");
        }
    }

    @GetMapping("/sent")
    public List<MailEntity> getSentEmails(@RequestParam Long fromId) {
        UserEntity user = userService.findById(fromId);
        return mailService.getSentEmails(user);
    }

    @GetMapping("/inbox")
    public ResponseEntity<List<MailEntity>> getInbox(@RequestParam Long toId) {
        try {
            logger.info("Received get inbox request for userId: {}", toId);
            List<MailEntity> inbox = mailService.getInboxByToId(toId);
            logger.info("Inbox retrieved successfully for userId: {}", toId);
            return ResponseEntity.ok(inbox);
        } catch (Exception e) {
            logger.error("Error retrieving inbox for userId: {}", toId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/read/{emailId}")
    public ResponseEntity<String> markAsRead(@PathVariable Long emailId) {
        try {
            Optional<MailEntity> mailOptional = mailService.findById(emailId);
            if (mailOptional.isPresent()) {
                MailEntity mail = mailOptional.get();
                mail.setRead(true);
                mailService.saveMail(mail);
                return ResponseEntity.ok("Mail marked as read");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mail not found");
            }
        } catch (Exception e) {
            logger.error("Error marking mail as read", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error marking mail as read");
        }
    }

    @GetMapping("/{emailId}")
    public ResponseEntity<MailEntity> getEmailById(@PathVariable Long emailId) {
        try {
            MailEntity mail = mailService.findById(emailId)
                    .orElseThrow(() -> new RuntimeException("Email not found"));
            return ResponseEntity.ok(mail);
        } catch (Exception e) {
            logger.error("Error retrieving email: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


}
