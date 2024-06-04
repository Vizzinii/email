package com.example.email.service;

import com.example.email.entity.FolderEntity;
import com.example.email.entity.MailEntity;
import com.example.email.entity.UserEntity;
import com.example.email.mailmanagement.beans.MailBean;
import com.example.email.repository.FolderRepository;
import com.example.email.repository.MailRepository;
import com.example.email.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;
@Service
public class MailService {

    @Autowired
    private MailRepository mailRepository;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private UserRepository userRepository;

    public Long getUserIdByUsername(String username) {
        Optional<UserEntity> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            return userOpt.get().getUserId();
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public List<MailEntity> getInboxByToId(Long toId) {
        Optional<FolderEntity> inboxFolderOpt = folderRepository.findByUserUserIdAndName(toId, "收件箱");
        if (inboxFolderOpt.isPresent()) {
            return mailRepository.findByToUserUserIdAndFolderFolderId(toId, inboxFolderOpt.get().getFolderId());
        } else {
            throw new RuntimeException("Inbox folder not found");
        }
    }


    public void sendEmail(UserEntity fromUser, UserEntity toUser, String subject, String body) {
        MailEntity mail = new MailEntity();
        mail.setFromUser(fromUser);
        mail.setToUser(toUser);
        mail.setFromEmail(fromUser.getEmail());
        mail.setToEmail(toUser.getEmail());
        mail.setSubject(subject);
        mail.setBody(body);
        mail.setSentDate(LocalDateTime.now());
        mail.setRead(false);
        mailRepository.save(mail);
    }



        public List<MailEntity> getInbox(UserEntity user) {
        return mailRepository.findByToUserWithFromUser(user);
    }

    public void deleteEmail(Long emailId) {
        if (mailRepository.existsById(emailId)) {
            mailRepository.deleteById(emailId);
        } else {
            throw new RuntimeException("Email not found");
        }
    }



    public void saveReceivedEmail(MailBean mailBean, Long userId) {
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
        mail.setFromUser(user); // assuming user is the sender
        mail.setToUser(user); // assuming user is the receiver
        mail.setFromEmail(mailBean.getFrom());
        mail.setToEmail(mailBean.getTo());
        mail.setSubject(mailBean.getSubject());
        mail.setBody(mailBean.getBody());
        mail.setSentDate(mailBean.getSentDate());
        mail.setRead(false);
        mail.setFolder(inboxFolder);

        mailRepository.save(mail);
    }
}