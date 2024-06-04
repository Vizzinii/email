package com.example.email.entity;

import jakarta.persistence.*;


import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "fromUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MailEntity> sentEmails;

    @OneToMany(mappedBy = "toUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MailEntity> receivedEmails;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FolderEntity> folders;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<MailEntity> getSentEmails() {
        return sentEmails;
    }

    public void setSentEmails(List<MailEntity> sentEmails) {
        this.sentEmails = sentEmails;
    }

    public List<MailEntity> getReceivedEmails() {
        return receivedEmails;
    }

    public void setReceivedEmails(List<MailEntity> receivedEmails) {
        this.receivedEmails = receivedEmails;
    }

    public List<FolderEntity> getFolders() {
        return folders;
    }

    public void setFolders(List<FolderEntity> folders) {
        this.folders = folders;
    }
}