package com.example.email.mailmanagement.util;

import java.io.File;
import java.nio.file.Files;
import java.util.Base64;

public class MailUtil {

    public static String encodeAttachment(File file) {
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            return Base64.getEncoder().encodeToString(fileContent);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encode file", e);
        }
    }

    public static File decodeAttachment(String encodedFile, String outputFilePath) {
        try {
            byte[] fileContent = Base64.getDecoder().decode(encodedFile);
            File file = new File(outputFilePath);
            Files.write(file.toPath(), fileContent);
            return file;
        } catch (Exception e) {
            throw new RuntimeException("Failed to decode file", e);
        }
    }
}