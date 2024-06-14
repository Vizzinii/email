package com.example.email.repository;

import com.example.email.entity.DraftEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DraftRepository extends JpaRepository<DraftEntity, Long> {  // 改为 Long 类型以匹配 BIGINT
    List<DraftEntity> findByUserId(Long userId);  // 改为 Long 类型以匹配 BIGINT

    @Query("SELECT d FROM DraftEntity d WHERE d.userId = :userId AND (d.subject LIKE %:keyword% OR d.body LIKE %:keyword% OR d.toEmail LIKE %:keyword% )")
    List<DraftEntity> findByUserIdAndKeyword(@Param("userId") Long userId, @Param("keyword") String keyword);
}