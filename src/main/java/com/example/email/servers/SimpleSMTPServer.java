package com.example.email.servers;

import com.example.email.repository.MailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Component
public class SimpleSMTPServer {
    private static final int SMTP_PORT = 2525;

    @Autowired
    private MailRepository mailRepository;

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(SMTP_PORT)) {
            System.out.println("SMTP Server is listening on port " + SMTP_PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                new SimpleSMTPServerHandler(socket, mailRepository).start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}