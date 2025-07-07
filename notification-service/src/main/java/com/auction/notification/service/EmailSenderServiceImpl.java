package com.auction.notification.service;

import com.auction.notification.model.Notification;
import com.auction.notification.repository.NotificationRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class EmailSenderServiceImpl implements EmailSenderService {
    private final NotificationRepository notificationRepository;

    public EmailSenderServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Async
    public void sendEmail(String email, String subject, String text) {
        // Solo guardamos la notificación en BD, no enviamos email
        Notification notification = new Notification();
        notification.setEmail(email);
        notification.setSubject(subject);
        notification.setText(text);
        notification.setSentAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    @Async
    public void sendEmailWithSender(String email, String subject, String text, String sentBy) {
        // Solo guardamos la notificación en BD, no enviamos email
        Notification notification = new Notification();
        notification.setEmail(email);
        notification.setSubject(subject);
        notification.setText(text);
        notification.setSentAt(LocalDateTime.now());
        notification.setSentBy(sentBy);
        notificationRepository.save(notification);
    }
}
