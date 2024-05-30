package com.example.email.repository;

import com.example.email.entity.MailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MailRepository extends JpaRepository<MailEntity, Long> {
    List<MailEntity> findByUserIdAndFolderId(Long userId, Long folderId);
}