package com.auction.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "notification-service")
public interface NotificationServiceClient {
    @PostMapping("/notifications/email")
    String sendEmail(@RequestBody Object emailRequest, @RequestHeader("Authorization") String token);
}
