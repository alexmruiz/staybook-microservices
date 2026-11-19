package com.staybook.notifications.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    private static final String FROM = "no-reply@staybook.com";
    private static final String TO = "customer@example.com";
    private static final String SUBJECT = "Confirme su cuenta";
    private static final String TEXT = "Haga clic en el enlace para verificar su cuenta.";

    @Mock
    private JavaMailSender mailSender;

    private EmailService emailService;

    @BeforeEach
    void setUp() {
        emailService = new EmailService(mailSender);
    }

    @Test
    void sendEmail_whenMailIsAccepted_shouldBuildAndSendMessage() {
        emailService.sendEmail(FROM, TO, SUBJECT, TEXT);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage message = messageCaptor.getValue();
        assertEquals(FROM, message.getFrom());
        assertEquals(TO, message.getTo()[0]);
        assertEquals(SUBJECT, message.getSubject());
        assertEquals(TEXT, message.getText());
    }

    @Test
    void sendEmail_whenMailSenderFails_shouldPropagateException() {
        MailSendException mailException = new MailSendException("SMTP no disponible");
        doThrow(mailException).when(mailSender).send(any(SimpleMailMessage.class));

        MailSendException exception = assertThrows(
                MailSendException.class,
                () -> emailService.sendEmail(FROM, TO, SUBJECT, TEXT));

        assertEquals(mailException, exception);
        verify(mailSender).send(any(SimpleMailMessage.class));
    }
}
