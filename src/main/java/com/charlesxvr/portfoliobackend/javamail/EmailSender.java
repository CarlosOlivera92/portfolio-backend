package com.charlesxvr.portfoliobackend.javamail;

import com.charlesxvr.portfoliobackend.configs.EmailConfig;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class EmailSender {
    private JavaMailSender mailSender;
    Properties properties = EmailConfig.getProperties();
    Session session = Session.getInstance(properties, new Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(properties.getProperty("mail.smtp.user"), properties.getProperty("mail.smtp.password"));
        }
    });
    public EmailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String email, String subject, String content) throws MessagingException, UnsupportedEncodingException {
        try {
            // Crea un mensaje
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(properties.getProperty("mail.smtp.user")));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject(subject);
            message.setContent(content, "text/html; charset=utf-8");

            // Envía el mensaje
            Transport.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el correo electrónico: " + e.getMessage());
        }
    }
}
