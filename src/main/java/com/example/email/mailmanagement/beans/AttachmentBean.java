package com.example.email.mailmanagement.beans;

public class AttachmentBean {

    private Long id;  // 新增字段
    private String fileName;
    private String filePath;

    // Constructors
    public AttachmentBean() {
    }

    public AttachmentBean(Long id, String fileName, String filePath) {
        this.id = id;
        this.fileName = fileName;
        this.filePath = filePath;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
    @Override
    public String toString() {
        return "AttachmentBean{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                '}';
    }
}