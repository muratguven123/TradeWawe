package com.murat.tradewave.Service;


import com.murat.tradewave.dto.email.EmailCreateRequest;
import com.murat.tradewave.service.EmailSendService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailSendServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailSendService emailSendService;

    @Test
    void sendEmail_shouldSendMailWithCorrectData() {
        EmailCreateRequest request = new EmailCreateRequest();
        request.setSubject("Test subject");
        request.setText("Test body");

        emailSendService.sendEmail(request);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage message = captor.getValue();
        assertEquals("muratguven376@gmail.com", message.getFrom());
        assertEquals("murat376@gmail.com", message.getTo()[0]);
        assertEquals("Test subject", message.getSubject());
        assertEquals("Test body", message.getText());
    }
}
