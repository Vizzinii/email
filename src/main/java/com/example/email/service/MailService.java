package com.example.email.service;

import com.example.email.entity.AttachmentEntity;
import com.example.email.entity.FolderEntity;
import com.example.email.entity.MailEntity;
import com.example.email.entity.UserEntity;
import com.example.email.mailmanagement.beans.MailBean;
import com.example.email.repository.AttachmentRepository;
import com.example.email.repository.FolderRepository;
import com.example.email.repository.MailRepository;
import com.example.email.repository.UserRepository;
import com.example.email.mailmanagement.beans.AttachmentBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    @Autowired
    private MailRepository mailRepository;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private AttachmentService attachmentService;

    public void sendEmail(UserEntity fromUser, UserEntity toUser, String subject, String body, List<AttachmentBean> attachments) {
        MailEntity mail = new MailEntity();
        mail.setFromUser(fromUser);
        mail.setToUser(toUser);
        mail.setFromEmail(fromUser.getEmail());
        mail.setToEmail(toUser.getEmail());
        mail.setSubject(subject);
        mail.setBody(body);
        mail.setSentDate(LocalDateTime.now());
        mail.setRead(false);

        if (attachments != null && !attachments.isEmpty()) {
            if (attachments.size() > 0) {
                Long attachmentId1 = attachments.get(0).getId();
                AttachmentEntity attachment1 = attachmentRepository.findById(attachmentId1)
                        .orElseThrow(() -> new RuntimeException("Attachment not found"));
                mail.setAttachment1(attachment1);
            }
            if (attachments.size() > 1) {
                Long attachmentId2 = attachments.get(1).getId();
                AttachmentEntity attachment2 = attachmentRepository.findById(attachmentId2)
                        .orElseThrow(() -> new RuntimeException("Attachment not found"));
                mail.setAttachment2(attachment2);
            }
            if (attachments.size() > 2) {
                Long attachmentId3 = attachments.get(2).getId();
                AttachmentEntity attachment3 = attachmentRepository.findById(attachmentId3)
                        .orElseThrow(() -> new RuntimeException("Attachment not found"));
                mail.setAttachment3(attachment3);
            }
        }

        mailRepository.save(mail);
    }

    public List<MailEntity> getInbox(UserEntity user) {
        return mailRepository.findByToUserWithFromUser(user);
    }

    public List<MailEntity> searchSentEmails(Long userId, String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            UserEntity user = new UserEntity();
            user.setUserId(userId);
            return mailRepository.findByFromUser(user);
        } else {
            return mailRepository.findByFromUserIdAndKeyword(userId, keyword);
        }
    }

    public List<MailEntity> getSentEmails(UserEntity user) {
        return mailRepository.findByFromUser(user);
    }

    public Long getUserIdByUsername(String username) {
        logger.info("Fetching userId for username: {}", username);
        Optional<UserEntity> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            return userOpt.get().getUserId();
        } else {
            logger.error("User not found for username: {}", username);
            throw new RuntimeException("User not found");
        }
    }

    public List<MailEntity> getInboxByToId(Long toId) {
        logger.info("Fetching inbox for userId: {}", toId);
        Optional<FolderEntity> inboxFolderOpt = folderRepository.findByUserUserIdAndName(toId, "收件箱");
        if (inboxFolderOpt.isPresent()) {
            List<MailEntity> mails = mailRepository.findByToUserUserIdAndFolderFolderIdOrderBySentDateDesc(toId, inboxFolderOpt.get().getFolderId());
            mails.forEach(mail -> logger.info("Mail ID: {}, Subject: {}, isRead: {}", mail.getEmailId(), mail.getSubject(), mail.isRead()));
            return mails;
        } else {
            logger.error("Inbox folder not found for userId: {}", toId);
            throw new RuntimeException("Inbox folder not found");
        }
    }

    public void saveMail(MailEntity mailEntity) {
        logger.info("Saving mail from {} to {}", mailEntity.getFromEmail(), mailEntity.getToEmail());
        mailRepository.save(mailEntity);
    }

    public void saveReceivedEmail(MailBean mailBean, Long userId) {
        logger.info("Saving received email for userId: {}", userId);
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Optional<FolderEntity> inboxFolderOpt = folderRepository.findByUserUserIdAndName(userId, "收件箱");
        FolderEntity inboxFolder;
        if (inboxFolderOpt.isPresent()) {
            inboxFolder = inboxFolderOpt.get();
        } else {
            inboxFolder = new FolderEntity();
            inboxFolder.setUser(user);
            inboxFolder.setName("收件箱");
            inboxFolder = folderRepository.save(inboxFolder);
        }

        MailEntity mail = new MailEntity();
        mail.setFromUser(user);
        mail.setToUser(user);
        mail.setFromEmail(mailBean.getFrom());
        mail.setToEmail(mailBean.getTo());
        mail.setSubject(mailBean.getSubject());
        mail.setBody(mailBean.getBody());
        mail.setSentDate(LocalDateTime.now());
        mail.setRead(false);
        mail.setFolder(inboxFolder);

        mailRepository.save(mail);
    }

    public void deleteEmail(Long emailId) {
        logger.info("Deleting email with emailId: {}", emailId);
        if (mailRepository.existsById(emailId)) {
            mailRepository.deleteById(emailId);
        } else {
            logger.error("Email not found with emailId: {}", emailId);
            throw new RuntimeException("Email not found");
        }
    }

    public Optional<MailEntity> findById(Long emailId) {
        return mailRepository.findById(emailId);
    }

    public List<MailEntity> searchEmails(Long userId, String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return mailRepository.findAllByToUserUserId(userId);
        } else {
            return mailRepository.findByToUserUserIdAndKeyword(userId, keyword);
        }
    }
}