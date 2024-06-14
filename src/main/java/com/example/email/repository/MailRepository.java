package com.example.email.repository;

import com.example.email.entity.AttachmentEntity;
import com.example.email.entity.MailEntity;
import com.example.email.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MailRepository extends JpaRepository<MailEntity, Long> {
    List<MailEntity> findByToUserUserIdAndFolderFolderId(Long toUserId, Long folderId);
    List<MailEntity> findAllByToUserUserId(Long userId);
    List<MailEntity> findByFromUser(UserEntity fromUser);

    @Query("SELECT m FROM MailEntity m WHERE m.toUser.userId = :userId AND (m.subject LIKE %:keyword% OR m.body LIKE %:keyword% OR m.fromEmail LIKE %:keyword%)")
    List<MailEntity> findByToUserUserIdAndKeyword(Long userId, String keyword);

    @Query("SELECT m FROM MailEntity m WHERE m.fromUser.userId = :fromUserId AND (m.subject LIKE %:keyword% OR m.body LIKE %:keyword% OR m.toEmail LIKE %:keyword%)")
    List<MailEntity> findByFromUserIdAndKeyword(@Param("fromUserId") Long fromUserId, @Param("keyword") String keyword);

    @Query("SELECT m FROM MailEntity m JOIN FETCH m.fromUser WHERE m.toUser = :toUser")
    List<MailEntity> findByToUserWithFromUser(@Param("toUser") UserEntity toUser);

    @Query("SELECT COUNT(m) FROM MailEntity m WHERE m.toUser = :user AND m.Read = false")
    long countUnreadMailsByUser(UserEntity user);

    List<MailEntity> findByToUserUserIdAndFolderFolderIdOrderBySentDateDesc(Long toUserId, Long folderId);
    List<MailEntity> findByAttachment1(AttachmentEntity attachment);
    List<MailEntity> findByAttachment2(AttachmentEntity attachment);
    List<MailEntity> findByAttachment3(AttachmentEntity attachment);// 添加按发送时间倒序排序的方法




}





