package com.learnsyncai.agent.service;

import com.learnsyncai.agent.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendReminder(User user, String message) {
        if (fromEmail == null || fromEmail.isBlank()) {
            log.info("Reminder fallback (mail disabled) to {}: {}", user.getEmail(), message);
            return;
        }
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(fromEmail);
            mailMessage.setTo(user.getEmail());
            mailMessage.setSubject("Learning Reminder");
            mailMessage.setText(message);
            mailSender.send(mailMessage);
        } catch (Exception ex) {
            log.warn("Unable to send reminder email to {}: {}", user.getEmail(), ex.getMessage());
        }
    }
}
