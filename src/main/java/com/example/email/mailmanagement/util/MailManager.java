package com.example.email.mailmanagement.util;

import com.example.email.entity.AttachmentEntity;
import com.example.email.entity.MailEntity;
import com.example.email.entity.UserEntity;
import com.example.email.mailmanagement.beans.AttachmentBean;
import com.example.email.mailmanagement.beans.MailBean;
import com.example.email.repository.AttachmentRepository;
import com.example.email.repository.MailRepository;
import com.example.email.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import jakarta.mail.internet.*;


import java.nio.file.Files;
import java.nio.file.Paths;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Component
public class MailManager {

    @Value("${mail.smtp.host}")
    private String smtpHost;

    @Value("${mail.smtp.port}")
    private int smtpPort;

    @Value("${mail.smtp.username}")
    private String smtpUsername;

    @Value("${mail.smtp.password}")
    private String smtpPassword;

    @Value("${mail.pop3.host}")
    private String pop3Host;

    @Value("${mail.pop3.port}")
    private int pop3Port;

    @Value("${mail.pop3.username}")
    private String pop3Username;

    @Value("${mail.pop3.password}")
    private String pop3Password;

    @Autowired
    private MailRepository mailRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AttachmentRepository attachmentRepository;

    private Properties smtpProps;
    private Properties pop3Props;

    @PostConstruct
    public void init() {
        smtpProps = new Properties();
        smtpProps.put("mail.smtp.auth", "true");
        smtpProps.put("mail.smtp.starttls.enable", "false");
        smtpProps.put("mail.smtp.host", smtpHost);
        smtpProps.put("mail.smtp.port", smtpPort);
        smtpProps.put("mail.smtp.connection timeout", "10000"); // 连接超时：10秒
        smtpProps.put("mail.smtp.timeout", "30000"); // 读超时：30秒

        pop3Props = new Properties();
        pop3Props.put("mail.pop3.host", pop3Host);
        pop3Props.put("mail.pop3.port", pop3Port);
        pop3Props.put("mail.pop3.starttls.enable", "false");
        pop3Props.put("mail.pop3.connection timeout", "10000"); // 连接超时：10秒
        pop3Props.put("mail.pop3.timeout", "30000"); // 读超时：30秒
    }

    public void sendEmail(MailBean mailBean) {
        try {
            Session session = Session.getInstance(smtpProps, new jakarta.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(smtpUsername, smtpPassword);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mailBean.getFrom()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailBean.getTo()));
            message.setSubject(mailBean.getSubject());

            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(mailBean.getBody());

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);

            // 处理附件
            if (mailBean.getAttachments() != null && !mailBean.getAttachments().isEmpty()) {
                for (AttachmentBean attachmentBean : mailBean.getAttachments()) {
                    AttachmentEntity attachment = attachmentRepository.findById(attachmentBean.getId())
                            .orElseThrow(() -> new RuntimeException("Attachment not found"));

                    MimeBodyPart attachmentPart = new MimeBodyPart();
                    attachmentPart.attachFile(Paths.get(attachment.getFilePath()).toFile());
                    multipart.addBodyPart(attachmentPart);
                }
            }

            message.setContent(multipart);

            Transport.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Error sending email", e);
        }
    }

    public List<MailBean> receiveEmails(String username, String password) {
        List<MailBean> emails = new ArrayList<>();
        Session session = Session.getInstance(pop3Props);
        Store store = null;
        Folder emailFolder = null;

        try {
            store = session.getStore("pop3");
            store.connect(pop3Host, username, password);

            emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);

            Message[] messages = emailFolder.getMessages();
            for (Message message : messages) {
                MailBean mail = new MailBean();
                mail.setFrom(((InternetAddress) message.getFrom()[0]).getAddress());
                mail.setTo(InternetAddress.toString(message.getRecipients(Message.RecipientType.TO)));
                mail.setSubject(message.getSubject());
                mail.setBody(message.getContent().toString());
                mail.setSentDate(message.getSentDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                emails.add(mail);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (emailFolder != null && emailFolder.isOpen()) {
                    emailFolder.close(false);
                }
                if (store != null) {
                    store.close();
                }
            } catch (MessagingException ex) {
                ex.printStackTrace();
            }
        }
        return emails;
    }
}


