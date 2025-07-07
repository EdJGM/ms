package com.auction.notification.service;

import com.auction.notification.dto.NotificationRequest;
import com.auction.notification.model.Notification;
import com.auction.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    
    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }
    
    @Override
    public List<Notification> getNotificationsByUser(String username) {
        return notificationRepository.findBySentBy(username);
    }
    
    @Override
    public List<Notification> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserId(userId);
    }
    
    @Override
    public Notification sendNotificationToUser(NotificationRequest request, String sentBy) {
        Notification notification = new Notification();
        notification.setUserId(request.getUserId());
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        notification.setType(request.getType());
        notification.setSentBy(sentBy);
        notification.setSentAt(LocalDateTime.now());
        
        return notificationRepository.save(notification);
    }
}
