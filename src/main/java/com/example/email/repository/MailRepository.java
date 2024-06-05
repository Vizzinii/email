package com.example.email.repository;

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
    @Query("SELECT m FROM MailEntity m JOIN FETCH m.fromUser WHERE m.toUser = :toUser")
    List<MailEntity> findByToUserWithFromUser(@Param("toUser") UserEntity toUser);
    List<MailEntity> findByToUserUserIdAndFolderFolderIdOrderBySentDateDesc(Long toUserId, Long folderId); // 添加按发送时间倒序排序的方法
}





