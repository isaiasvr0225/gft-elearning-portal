package com.gft.application.utils;

public interface MailSenderService {
    void sendMessage(String userEmail, String subject, String message);
    void sendHtmlMessage(String userEmail, String subject, String htmlContent);
}
