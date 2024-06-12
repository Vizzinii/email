package com.example.email.service;

import com.example.email.entity.AttachmentEntity;
import com.example.email.entity.MailEntity;
import com.example.email.repository.AttachmentRepository;
import com.example.email.repository.MailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Service
public class AttachmentService {

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private MailRepository mailRepository;

    public void deleteAttachment(Long attachmentId) {
        Optional<AttachmentEntity> attachmentOpt = attachmentRepository.findById(attachmentId);
        if (!attachmentOpt.isPresent()) {
            throw new RuntimeException("Attachment not found");
        }

        AttachmentEntity attachment = attachmentOpt.get();

        // 更新所有引用该附件的邮件，将所有 attachment 字段设置为 NULL
        updateEmailsWithAttachment(attachment);

        // 删除附件文件
        File file = new File(attachment.getFilePath());
        if (file.exists()) {
            file.delete();
        }

        // 删除附件实体
        attachmentRepository.delete(attachment);
    }

    private void updateEmailsWithAttachment(AttachmentEntity attachment) {
        List<MailEntity> emailsWithAttachment1 = mailRepository.findByAttachment1(attachment);
        for (MailEntity email : emailsWithAttachment1) {
            email.setAttachment1(null);
            mailRepository.save(email);
        }

        List<MailEntity> emailsWithAttachment2 = mailRepository.findByAttachment2(attachment);
        for (MailEntity email : emailsWithAttachment2) {
            email.setAttachment2(null);
            mailRepository.save(email);
        }

        List<MailEntity> emailsWithAttachment3 = mailRepository.findByAttachment3(attachment);
        for (MailEntity email : emailsWithAttachment3) {
            email.setAttachment3(null);
            mailRepository.save(email);
        }
    }
}