package com.example.email.controller;

import com.example.email.entity.AttachmentEntity;
import com.example.email.entity.MailEntity;
import com.example.email.entity.UserEntity;
import com.example.email.repository.AttachmentRepository;
import com.example.email.repository.UserRepository;
import com.example.email.service.UserService;
import com.example.email.service.AttachmentService;
import com.example.email.repository.MailRepository;

import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/attachments")
public class AttachmentController {

    private static final Logger logger = LoggerFactory.getLogger(AttachmentController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired MailRepository mailRepository;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                        @RequestParam("userId") Long userId) {
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        UserEntity user = userOpt.get();
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file selected");
        }

        String fileName = file.getOriginalFilename();
        // 使用用户的 attachmentFolderPath 保存文件
        String userAttachmentFolderPath = user.getAttachmentFolderPath();
        Path filePath = Paths.get(userAttachmentFolderPath, fileName);
        File attachmentFile = filePath.toFile();

        if (attachmentFile.exists()) {
            return ResponseEntity.badRequest().body("File already exists");
        }

        try {
            file.transferTo(attachmentFile);

            AttachmentEntity attachment = new AttachmentEntity();
            attachment.setUser(user);
            attachment.setFileName(fileName);
            attachment.setFilePath(filePath.toString());
            attachment.setUploadedAt(LocalDateTime.now());
            attachmentRepository.save(attachment);

            return ResponseEntity.ok(attachment);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("File upload failed");
        }
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AttachmentEntity>> getUserAttachments(@PathVariable Long userId) {
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        UserEntity user = userOpt.get();
        List<AttachmentEntity> attachments = attachmentRepository.findByUserOrderByUploadedAtDesc(user);
        return ResponseEntity.ok(attachments);
    }

    @DeleteMapping("/{attachmentId}")
    public ResponseEntity<?> deleteAttachment(@PathVariable Long attachmentId) {
        try {
            attachmentService.deleteAttachment(attachmentId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/download/{attachmentId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long attachmentId) {
        Optional<AttachmentEntity> attachmentOpt = attachmentRepository.findById(attachmentId);
        if (!attachmentOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        AttachmentEntity attachment = attachmentOpt.get();
        Path filePath = Paths.get(attachment.getFilePath());
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            String originalFileName = attachment.getFileName();
            String encodedFileName = URLEncoder.encode(originalFileName, StandardCharsets.UTF_8.toString());

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}