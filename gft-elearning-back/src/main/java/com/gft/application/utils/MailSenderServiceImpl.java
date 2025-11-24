package com.gft.application.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MailSenderServiceImpl implements MailSenderService {

    @Value("${mail.sender.email}")
    private String senderEmail;

    private final JavaMailSender javaMailSender;

    @Override
    public void sendMessage(String userEmail, String subject, String message) {

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setFrom(senderEmail);
        simpleMailMessage.setTo(userEmail);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);

        javaMailSender.send(simpleMailMessage);

    }

    @Override
    public void sendHtmlMessage(String userEmail, String subject, String htmlContent) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(userEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true indica que es HTML

            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el correo");
        }
    }
}
