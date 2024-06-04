package com.example.email.entity;

import jakarta.persistence.*;
import java.util.List;


@Entity
@Table(name = "folders")
public class FolderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long folderId;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MailEntity> emails;

    // Getters and Setters
    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public List<MailEntity> getEmails() {
        return emails;
    }

    public void setEmails(List<MailEntity> emails) {
        this.emails = emails;
    }
}