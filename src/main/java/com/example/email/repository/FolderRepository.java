package com.example.email.repository;

import com.example.email.entity.FolderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface FolderRepository extends JpaRepository<FolderEntity, Long> {
    Optional<FolderEntity> findByUserUserIdAndName(Long userId, String name);
}