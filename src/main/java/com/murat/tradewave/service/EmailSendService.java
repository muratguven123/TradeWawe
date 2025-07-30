package com.murat.tradewave.service;

import com.murat.tradewave.dto.email.EmailCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSendService {
    private final JavaMailSender mailSender;
    public void sendEmail(EmailCreateRequest emailCreateRequest) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("muratguven376@gmail.com");
        message.setTo("murat376@gmail.com");
        message.setSubject(emailCreateRequest.getSubject());
        message.setText(emailCreateRequest.getText());
        mailSender.send(message);
    }
}
