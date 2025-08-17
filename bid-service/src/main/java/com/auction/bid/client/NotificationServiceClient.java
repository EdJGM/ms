package com.auction.bid.client;

import com.auction.bid.dto.NewBidEventDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "notification-service")
public interface NotificationServiceClient {
    
    @PostMapping("/notifications/email")
    String sendEmail(@RequestBody Object emailRequest, @RequestHeader("Authorization") String token);
    
    @PostMapping("/ws/broadcast/new-bid")
    void broadcastNewBid(@RequestBody NewBidEventDto eventDto, 
                        @RequestHeader("Authorization") String token);
}
