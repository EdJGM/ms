package com.auction.auction.client;

import com.auction.auction.dto.AuctionExtendedEventDto;
import com.auction.auction.dto.ModeratorJoinedEventDto;
import com.auction.auction.dto.AuctionFinishedEventDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "notification-service")
public interface NotificationServiceClient {
    
    @PostMapping("/notifications/email")
    String sendEmail(@RequestBody Object emailRequest, @RequestHeader("Authorization") String token);
    
    @PostMapping("/ws/broadcast/auction-extended")
    void broadcastAuctionExtended(@RequestBody AuctionExtendedEventDto eventDto,
                                 @RequestHeader("Authorization") String token);
    
    @PostMapping("/ws/broadcast/moderator-joined")
    void broadcastModeratorJoined(@RequestBody ModeratorJoinedEventDto eventDto,
                                 @RequestHeader("Authorization") String token);
    
    @PostMapping("/ws/broadcast/auction-finished")
    void broadcastAuctionFinished(@RequestBody AuctionFinishedEventDto eventDto,
                                 @RequestHeader("Authorization") String token);
}
