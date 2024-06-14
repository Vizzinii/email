package com.example.email.repository;

import com.example.email.entity.AttachmentEntity;
import com.example.email.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface AttachmentRepository extends JpaRepository<AttachmentEntity, Long> {
    List<AttachmentEntity> findByUserOrderByUploadedAtDesc(UserEntity user);
    List<AttachmentEntity> findByUser(UserEntity user);

    @Query("SELECT a FROM AttachmentEntity a WHERE a.user.userId = :userId AND (a.fileName LIKE %:keyword%) ORDER BY a.uploadedAt DESC")
    List<AttachmentEntity> findByUserIdAndKeyword(@Param("userId") Long userId, @Param("keyword") String keyword);
}