package com.example.email.repository;

import com.example.email.entity.FolderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FolderRepository extends JpaRepository<FolderEntity, Long> {
}