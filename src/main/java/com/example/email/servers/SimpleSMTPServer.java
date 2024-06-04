package com.example.email.servers;

import com.example.email.repository.MailRepository;
import com.example.email.repository.UserRepository;
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

    @Autowired
    private UserRepository userRepository;

    private ServerSocket serverSocket;
    private boolean running = true;

    public void start() {
        try {
            serverSocket = new ServerSocket(SMTP_PORT);
            System.out.println("SMTP Server is listening on port " + SMTP_PORT);
            while (running) {
                Socket socket = serverSocket.accept();
                new SimpleSMTPServerHandler(socket, mailRepository, userRepository).start();
            }
        } catch (IOException ex) {
            if (running) {
                ex.printStackTrace();
            }
        } finally {
            stop();
        }
    }

    public void stop() {
        running = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        System.out.println("SMTP Server stopped.");
    }
}