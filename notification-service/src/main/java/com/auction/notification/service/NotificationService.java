package com.auction.notification.service;

import com.auction.notification.dto.NotificationRequest;
import com.auction.notification.model.Notification;
import java.util.List;

public interface NotificationService {
    List<Notification> getNotificationsByUser(String username);
    List<Notification> getNotificationsByUserId(Long userId);
    Notification sendNotificationToUser(NotificationRequest request, String sentBy);
}
