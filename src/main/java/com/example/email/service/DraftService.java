package com.example.email.service;

import com.example.email.entity.DraftEntity;
import com.example.email.repository.DraftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DraftService {

    @Autowired
    private DraftRepository draftRepository;

    public List<DraftEntity> getDraftsByUserId(Long userId, String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return draftRepository.findByUserId(userId);
        } else {
            return draftRepository.findByUserIdAndKeyword(userId, keyword);
        }
    }

    public DraftEntity saveDraft(DraftEntity draft) {
        return draftRepository.save(draft);
    }

    public Optional<DraftEntity> getDraftById(Long draftId) {
        return draftRepository.findById(draftId);
    }

    public void deleteDraft(Long draftId) {
        draftRepository.deleteById(draftId);
    }
}