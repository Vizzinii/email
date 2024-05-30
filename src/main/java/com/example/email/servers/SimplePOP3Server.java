package com.example.email.servers;

import com.example.email.repository.MailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Component
public class SimplePOP3Server {
    private static final int POP3_PORT = 1100;

    @Autowired
    private MailRepository mailRepository;

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(POP3_PORT)) {
            System.out.println("POP3 Server is listening on port " + POP3_PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                new SimplePOP3ServerHandler(socket, mailRepository).start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

