package com.auction.notification.repository;

import com.auction.notification.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    java.util.List<com.auction.notification.model.Notification> findBySentBy(String sentBy);
    java.util.List<com.auction.notification.model.Notification> findByUserId(Long userId);
}
