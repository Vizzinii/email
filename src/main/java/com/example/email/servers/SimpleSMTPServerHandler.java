package com.example.email.servers;

import com.example.email.entity.MailEntity;
import com.example.email.entity.UserEntity;
import com.example.email.repository.MailRepository;
import com.example.email.repository.UserRepository;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Optional;

public class SimpleSMTPServerHandler extends Thread {
    private Socket socket;
    private MailRepository mailRepository;
    private UserRepository userRepository;

    public SimpleSMTPServerHandler(Socket socket, MailRepository mailRepository, UserRepository userRepository) {
        this.socket = socket;
        this.mailRepository = mailRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run() {
        try (InputStream input = socket.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(input));
             OutputStream output = socket.getOutputStream();
             PrintWriter writer = new PrintWriter(output, true)) {

            writer.println("220 Simple SMTP Server");

            String senderEmail = null;
            String recipientEmail = null;
            String subject = null;
            StringBuilder data = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("HELO") || line.startsWith("EHLO")) {
                    writer.println("250 Hello " + line.substring(5));
                } else if (line.startsWith("MAIL FROM:")) {
                    senderEmail = line.substring(10).trim();
                    writer.println("250 OK");
                } else if (line.startsWith("RCPT TO:")) {
                    recipientEmail = line.substring(8).trim();
                    writer.println("250 OK");
                } else if (line.startsWith("DATA")) {
                    writer.println("354 End data with <CR><LF>.<CR><LF>");
                } else if (line.equals(".")) {
                    writer.println("250 OK: Message accepted for delivery");
                    if (senderEmail != null && recipientEmail != null) {
                        Optional<UserEntity> senderOpt = userRepository.findByEmail(senderEmail);
                        Optional<UserEntity> recipientOpt = userRepository.findByEmail(recipientEmail);
                        if (senderOpt.isPresent() && recipientOpt.isPresent()) {
                            MailEntity mail = new MailEntity();
                            mail.setFromUser(senderOpt.get());
                            mail.setToUser(recipientOpt.get());
                            mail.setFromEmail(senderEmail);
                            mail.setToEmail(recipientEmail);
                            mail.setSubject(subject);
                            mail.setBody(data.toString());
                            mail.setSentDate(LocalDateTime.now());
                            mail.setRead(false);
                            mailRepository.save(mail);
                            System.out.println("Saved email to database.");
                        } else {
                            System.out.println("Sender or recipient not found in the database.");
                        }
                    }
                } else if (line.startsWith("QUIT")) {
                    writer.println("221 Bye");
                    break;
                } else if (line.startsWith("Subject:")) {
                    subject = line.substring(8).trim();
                } else {
                    data.append(line).append("\n");
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}