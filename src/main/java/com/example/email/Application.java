package com.example.email;

import com.example.email.servers.SimplePOP3Server;
import com.example.email.servers.SimpleSMTPServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner run(SimpleSMTPServer smtpServer, SimplePOP3Server pop3Server) {
        return args -> {
            new Thread(() -> {
                smtpServer.start();
            }).start();

            new Thread(() -> {
                pop3Server.start();
            }).start();
        };
    }
}