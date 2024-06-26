package com.example.email.mailmanagement.beans;

import java.time.LocalDateTime;
import java.util.List;

public class MailBean {

    private String subject;
    private String body;
    private String from;
    private String to;
    private LocalDateTime sentDate;
    private boolean Read;
    private List<AttachmentBean> attachments;

    // Constructors
    public MailBean() {
    }

    public MailBean(String subject, String body, String from, String to, LocalDateTime sentDate, boolean Read, List<AttachmentBean> attachments) {
        this.subject = subject;
        this.body = body;
        this.from = from;
        this.to = to;
        this.sentDate = sentDate;
        this.Read = Read;
        this.attachments = attachments;
    }

    // Getters and Setters
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public LocalDateTime getSentDate() {
        return sentDate;
    }

    public void setSentDate(LocalDateTime sentDate) {
        this.sentDate = sentDate;
    }

    public boolean Read() {
        return Read;
    }

    public void setRead(boolean read) {
        Read = read;
    }

    public List<AttachmentBean> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentBean> attachments) {
        this.attachments = attachments;
    }

    // Additional methods if needed

    @Override
    public String toString() {
        return "MailBean{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", sentDate=" + sentDate +
                '}';
    }
}