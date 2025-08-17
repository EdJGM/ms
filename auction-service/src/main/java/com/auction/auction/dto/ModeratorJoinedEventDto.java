package com.auction.auction.dto;

import java.time.LocalDateTime;

public class ModeratorJoinedEventDto {
    private String auctionId;
    private String moderatorName;
    private LocalDateTime joinTime;
    
    // Constructor por defecto
    public ModeratorJoinedEventDto() {}
    
    // Constructor con parámetros
    public ModeratorJoinedEventDto(String auctionId, String moderatorName, LocalDateTime joinTime) {
        this.auctionId = auctionId;
        this.moderatorName = moderatorName;
        this.joinTime = joinTime;
    }
    
    // Getters y setters
    public String getAuctionId() {
        return auctionId;
    }
    
    public void setAuctionId(String auctionId) {
        this.auctionId = auctionId;
    }
    
    public String getModeratorName() {
        return moderatorName;
    }
    
    public void setModeratorName(String moderatorName) {
        this.moderatorName = moderatorName;
    }
    
    public LocalDateTime getJoinTime() {
        return joinTime;
    }
    
    public void setJoinTime(LocalDateTime joinTime) {
        this.joinTime = joinTime;
    }
}
