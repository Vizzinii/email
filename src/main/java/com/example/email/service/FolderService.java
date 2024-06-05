package com.example.email.service;

import com.example.email.entity.FolderEntity;
import com.example.email.repository.FolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FolderService {
    @Autowired
    private FolderRepository folderRepository;

    /**
     * 根据用户ID查找收件箱Folder
     *
     * @param userId 用户ID
     * @return Optional 包含收件箱Folder的Optional对象
     */
    public Optional<FolderEntity> findInboxFolderByUserId(Long userId) {
        return folderRepository.findByUserUserIdAndName(userId, "收件箱");
    }
}