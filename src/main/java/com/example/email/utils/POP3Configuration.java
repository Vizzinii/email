package com.example.email.utils;

import com.example.email.utils.MailServerConfiguration;

public class POP3Configuration extends MailServerConfiguration {
    private POP3Configuration(String host, Integer port, String email, String password) {
        super(host, port, email, password);
    }

    public static POP3Configuration instance (String host, Integer port, String email, String password){
        return new POP3Configuration(host, port, email, password);
    }
}

