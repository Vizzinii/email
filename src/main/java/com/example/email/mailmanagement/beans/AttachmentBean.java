package com.example.email.mailmanagement.beans;

public class AttachmentBean {

    private String fileName;
    private String filePath;

    // Constructors
    public AttachmentBean() {
    }

    public AttachmentBean(String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
    }

    // Getters and Setters
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    // Additional methods if needed
}