package com.example.email.mailmanagement.util;

import com.example.email.entity.MailEntity;
import com.example.email.entity.UserEntity;
import com.example.email.mailmanagement.beans.MailBean;
import com.example.email.repository.MailRepository;
import com.example.email.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    private Properties smtpProps;
    private Properties pop3Props;

    @PostConstruct
    public void init() {
        smtpProps = new Properties();
        smtpProps.put("mail.smtp.auth", "true");
        smtpProps.put("mail.smtp.starttls.enable", "false");
        smtpProps.put("mail.smtp.host", smtpHost);
        smtpProps.put("mail.smtp.port", smtpPort);

        pop3Props = new Properties();
        pop3Props.put("mail.pop3.host", pop3Host);
        pop3Props.put("mail.pop3.port", pop3Port);
        pop3Props.put("mail.pop3.starttls.enable", "false");
    }

    public void sendEmail(MailBean mailBean) {
        MailEntity mail = new MailEntity();

        Optional<UserEntity> fromUserOpt = userRepository.findByEmail(mailBean.getFrom());
        Optional<UserEntity> toUserOpt = userRepository.findByEmail(mailBean.getTo());

        if (fromUserOpt.isPresent()) {
            UserEntity fromUser = fromUserOpt.get();
            mail.setFromUser(fromUser);
            mail.setFromEmail(mailBean.getFrom());
        } else {
            throw new RuntimeException("Sender not found");
        }

        if (toUserOpt.isPresent()) {
            UserEntity toUser = toUserOpt.get();
            mail.setToUser(toUser);
            mail.setToEmail(mailBean.getTo());
        } else {
            throw new RuntimeException("Recipient not found");
        }

        mail.setSubject(mailBean.getSubject());
        mail.setBody(mailBean.getBody());
        mail.setSentDate(mailBean.getSentDate());
        mail.setRead(false); // Assuming the email is unread when sent

        mailRepository.save(mail);
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
            emailFolder.open(Folder.READ_ONLY); // 确保文件夹已打开

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
            // 确保在操作完成后正确关闭文件夹和存储
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


