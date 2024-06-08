package com.example.email.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "emails")
public class MailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long emailId;

    @ManyToOne
    @JoinColumn(name = "from_id", nullable = false)
    @JsonBackReference(value = "from-user-emails")
    private UserEntity fromUser;

    @ManyToOne
    @JoinColumn(name = "to_id", nullable = false)
    @JsonBackReference(value = "to-user-emails")
    private UserEntity toUser;

    @Column(nullable = false, name = "from_email")
    private String fromEmail;

    @Column(nullable = false, name = "to_email")
    private String toEmail;

    private String subject;

    private String body;

    @Column(name = "sent_date")
    private LocalDateTime sentDate;

    @Column(name = "is_read")
    private boolean Read;

    @ManyToOne
    @JoinColumn(name = "folder_id")
    @JsonBackReference(value = "folder-emails")
    private FolderEntity folder;

    @ManyToOne
    @JoinColumn(name = "attachment_id", nullable = true)
    private AttachmentEntity attachment;

    // Getters and Setters
    public Long getEmailId() {
        return emailId;
    }

    public void setEmailId(Long emailId) {
        this.emailId = emailId;
    }

    public UserEntity getFromUser() {
        return fromUser;
    }

    public void setFromUser(UserEntity fromUser) {
        this.fromUser = fromUser;
    }

    public UserEntity getToUser() {
        return toUser;
    }

    public void setToUser(UserEntity toUser) {
        this.toUser = toUser;
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public String getToEmail() {
        return toEmail;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }

    public Long getToId() {
        return toUser.getUserId();
    }


    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public LocalDateTime getSentDate() {
        return sentDate;
    }

    public void setSentDate(LocalDateTime sentDate) {
        this.sentDate = sentDate;
    }

    public boolean isRead() {
        return Read;
    }

    public void setRead(boolean isRead) {
        this.Read = isRead;
    }

    public FolderEntity getFolder() {
        return folder;
    }

    public void setFolder(FolderEntity folder) {
        this.folder = folder;
    }

    public AttachmentEntity getAttachment() {
        return attachment;
    }

    public void setAttachment(AttachmentEntity attachment) {
        this.attachment = attachment;
    }
}