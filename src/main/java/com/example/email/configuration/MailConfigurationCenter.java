package com.example.email.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MailConfigurationCenter {

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

    public AbstractMailServerConfiguration loadConfiguration(String type) {
        if (type.equalsIgnoreCase("SMTP")) {
            return new SMTPConfiguration(smtpHost, smtpPort, smtpUsername, smtpPassword);
        } else if (type.equalsIgnoreCase("POP3")) {
            return new POP3Configuration(pop3Host, pop3Port, pop3Username, pop3Password);
        }
        throw new IllegalArgumentException("Invalid mail server type");
    }
}