package com.example.email.servers;

import com.example.email.entity.MailEntity;
import com.example.email.repository.MailRepository;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class SimplePOP3ServerHandler extends Thread {
    private Socket socket;
    private MailRepository mailRepository;

    public SimplePOP3ServerHandler(Socket socket, MailRepository mailRepository) {
        this.socket = socket;
        this.mailRepository = mailRepository;
    }

    @Override
    public void run() {
        try (InputStream input = socket.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(input));
             OutputStream output = socket.getOutputStream();
             PrintWriter writer = new PrintWriter(output, true)) {

            writer.println("+OK Simple POP3 Server ready");

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("USER")) {
                    writer.println("+OK");
                } else if (line.startsWith("PASS")) {
                    writer.println("+OK");
                } else if (line.startsWith("STAT")) {
                    long count = mailRepository.count();
                    writer.println("+OK " + count + " messages");
                } else if (line.startsWith("LIST")) {
                    List<MailEntity> messages = mailRepository.findAll();
                    writer.println("+OK " + messages.size() + " messages");
                    for (int i = 0; i < messages.size(); i++) {
                        writer.println((i + 1) + " " + messages.get(i).getBody().length());
                    }
                    writer.println(".");
                } else if (line.startsWith("RETR")) {
                    int index = Integer.parseInt(line.split(" ")[1]) - 1;
                    List<MailEntity> messages = mailRepository.findAll();
                    if (index >= 0 && index < messages.size()) {
                        MailEntity mail = messages.get(index);
                        writer.println("+OK " + mail.getBody().length() + " octets");
                        writer.println(mail.getBody());
                        writer.println(".");
                    } else {
                        writer.println("-ERR no such message");
                    }
                } else if (line.startsWith("DELE")) {
                    int index = Integer.parseInt(line.split(" ")[1]) - 1;
                    List<MailEntity> messages = mailRepository.findAll();
                    if (index >= 0 && index < messages.size()) {
                        MailEntity mail = messages.get(index);
                        mailRepository.delete(mail);
                        writer.println("+OK message " + (index + 1) + " deleted");
                    } else {
                        writer.println("-ERR no such message");
                    }
                } else if (line.startsWith("QUIT")) {
                    writer.println("+OK Bye");
                    break;
                } else {
                    writer.println("-ERR unknown command");
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
