package com.example.email.configuration;

public class SMTPConfiguration extends AbstractMailServerConfiguration {

    public SMTPConfiguration(String host, int port, String username, String password) {
        super(host, port, username, password);
    }

    @Override
    public void configure() {
        // SMTP-specific configuration logic
        System.out.println("Configuring SMTP server...");
        System.out.println("Host: " + host);
        System.out.println("Port: " + port);
        System.out.println("Username: " + username);
        // Don't print password for security reasons
    }
}