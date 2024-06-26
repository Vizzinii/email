package com.example.email.controller;

import com.example.email.entity.UserEntity;
import com.example.email.mailmanagement.beans.AttachmentBean;
import com.example.email.repository.AttachmentRepository;
import com.example.email.service.AttachmentService;
import com.example.email.service.MailService;
import com.example.email.service.UserService;
import com.example.email.service.DraftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Controller
public class ConsoleController implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private DraftService draftService;

    @Autowired
    private AttachmentRepository attachmentRepository;

    private Scanner scanner = new Scanner(System.in);

    @Override
    public void run(String... args) {
        showMenu();
    }

    private void showMenu() {
        while (true) {
            System.out.println("1. Register User");
            System.out.println("2. Login");
            System.out.println("3. Send Email");
            System.out.println("4. View Inbox");
            System.out.println("5. Delete User");
            System.out.println("6. Delete Email");
            System.out.println("7. Delete Attachment");
            System.out.println("8. Delete Draft");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (option) {
                case 1:
                    registerUser();
                    break;
                case 2:
                    login();
                    break;
                case 3:
                    sendEmail();
                    break;
                case 4:
                    viewInbox();
                    break;
                case 5:
                    deleteUser();
                    break;
                case 6:
                    deleteEmail();
                    break;
                case 7:
                    deleteAttachment();
                    break;
                case 8:
                    deleteDraft();
                    break;
                case 0:
                    System.out.println("Exiting...");
                    return; // Exit the run method to trigger application shutdown
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void registerUser() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        userService.registerUser(username, email, password);
        System.out.println("User registered successfully.");
    }

    private void login() {
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        UserEntity user = userService.authenticateUser(email, password);
        if (user != null) {
            System.out.println("Login successful.");
        } else {
            System.out.println("Invalid email or password.");
        }
    }

    private void sendEmail() {
        System.out.print("Enter your username: ");
        String fromUsername = scanner.nextLine();
        System.out.print("Enter recipient's username: ");
        String toUsername = scanner.nextLine();
        System.out.print("Enter subject: ");
        String subject = scanner.nextLine();
        System.out.print("Enter body: ");
        String body = scanner.nextLine();

        Optional<UserEntity> fromUserOpt = userService.findUserByUsername(fromUsername);
        Optional<UserEntity> toUserOpt = userService.findUserByUsername(toUsername);
        List<AttachmentBean> attachments = new ArrayList<>();

        if (fromUserOpt.isPresent() && toUserOpt.isPresent()) {
            mailService.sendEmail(fromUserOpt.get(), toUserOpt.get(), subject, body, attachments);
            System.out.println("Email sent successfully.");
        } else {
            System.out.println("Invalid sender or recipient username.");
        }
    }

    private void viewInbox() {
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();

        Optional<UserEntity> userOpt = userService.findUserByUsername(username);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            mailService.getInbox(user).forEach(mail -> {
                System.out.println("From: " + mail.getFromUser().getUsername());
                System.out.println("Subject: " + mail.getSubject());
                System.out.println("Body: " + mail.getBody());
                System.out.println("Date: " + mail.getSentDate());
                System.out.println("-----");
            });
        } else {
            System.out.println("Invalid username.");
        }
    }

    private void deleteUser() {
        System.out.print("Enter username to delete: ");
        String username = scanner.nextLine();

        Optional<UserEntity> userOpt = userService.findUserByUsername(username);
        if (userOpt.isPresent()) {
            userService.deleteUser(userOpt.get().getUserId());
            System.out.println("User deleted successfully.");
        } else {
            System.out.println("Invalid username.");
        }
    }

    private void deleteEmail() {
        System.out.print("Enter email ID to delete: ");
        Long emailId = scanner.nextLong();
        scanner.nextLine(); // consume newline
        try {
            mailService.deleteEmail(emailId);
            System.out.println("Email deleted successfully.");
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    private void deleteAttachment() {
        System.out.print("Enter attachment ID to delete: ");
        Long attachmentId = scanner.nextLong();
        scanner.nextLine(); // consume newline
        try {
            attachmentService.deleteAttachment(attachmentId);
            System.out.println("Attachment deleted successfully.");
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    private void deleteDraft() {
        System.out.print("Enter draft ID to delete: ");
        Long draftId = scanner.nextLong();
        scanner.nextLine(); // consume newline
        try {
            draftService.deleteDraft(draftId);
            System.out.println("Draft deleted successfully.");
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }
}