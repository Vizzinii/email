package com.example.email.configuration;

public class POP3Configuration extends AbstractMailServerConfiguration {

    public POP3Configuration(String host, int port, String username, String password) {
        super(host, port, username, password);
    }

    @Override
    public void configure() {
        // POP3-specific configuration logic
        System.out.println("Configuring POP3 server...");
        System.out.println("Host: " + host);
        System.out.println("Port: " + port);
        System.out.println("Username: " + username);
        // Don't print password for security reasons
    }
}