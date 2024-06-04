package com.example.email;

import com.example.email.servers.SimpleSMTPServer;
import com.example.email.servers.SimplePOP3Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PreDestroy;

@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    private SimpleSMTPServer smtpServer;

    @Autowired
    private SimplePOP3Server pop3Server;

    private Thread smtpThread;
    private Thread pop3Thread;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) {
        startServers();
    }

    private void startServers() {
        smtpThread = new Thread(() -> smtpServer.start());
        smtpThread.setDaemon(true);
        pop3Thread = new Thread(() -> pop3Server.start());
        pop3Thread.setDaemon(true);

        smtpThread.start();
        pop3Thread.start();
    }

    @PreDestroy
    public void onExit() {
        System.out.println("Stopping servers...");

        smtpServer.stop();
        pop3Server.stop();

        System.out.println("Servers stopped.");
    }
}