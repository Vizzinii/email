package com.example.email.service;

import com.example.email.entity.MailEntity;
import com.example.email.repository.MailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MailService {

    @Autowired
    private static MailRepository mailRepository;

    public static List<MailEntity> getInboxByUserId(Long userId) {
        return mailRepository.findByUserIdAndFolderId(userId, null);
    }
}