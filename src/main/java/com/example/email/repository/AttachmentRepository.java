package com.example.email.repository;

import com.example.email.entity.AttachmentEntity;
import com.example.email.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface AttachmentRepository extends JpaRepository<AttachmentEntity, Long> {
    List<AttachmentEntity> findByUserOrderByUploadedAtDesc(UserEntity user);
    List<AttachmentEntity> findByUser(UserEntity user);
}