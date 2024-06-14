package com.example.email.controller;

import com.example.email.entity.DraftEntity;
import com.example.email.service.DraftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/drafts")
public class DraftController {

    @Autowired
    private DraftService draftService;

    @GetMapping("/user/{userId}")
    public List<DraftEntity> getDraftsByUserId(@PathVariable Long userId, @RequestParam(required = false) String keyword) {
        return draftService.getDraftsByUserId(userId, keyword);
    }

    @PostMapping("/save")
    public DraftEntity saveDraft(@RequestBody DraftEntity draft) {
        return draftService.saveDraft(draft);
    }

    @GetMapping("/{draftId}")
    public DraftEntity getDraftById(@PathVariable Long draftId) {
        return draftService.getDraftById(draftId).orElse(null);
    }

    @DeleteMapping("/delete/{draftId}")
    public void deleteDraft(@PathVariable Long draftId) {
        draftService.deleteDraft(draftId);
    }
}