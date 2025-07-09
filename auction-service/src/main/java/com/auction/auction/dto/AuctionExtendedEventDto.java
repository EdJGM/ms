package com.auction.auction.dto;

import java.time.LocalDateTime;

public class AuctionExtendedEventDto {
    private String auctionId;
    private LocalDateTime newEndTime;
    private int minutesAdded;
    
    // Constructor por defecto
    public AuctionExtendedEventDto() {}
    
    // Constructor con parámetros
    public AuctionExtendedEventDto(String auctionId, LocalDateTime newEndTime, int minutesAdded) {
        this.auctionId = auctionId;
        this.newEndTime = newEndTime;
        this.minutesAdded = minutesAdded;
    }
    
    // Getters y setters
    public String getAuctionId() {
        return auctionId;
    }
    
    public void setAuctionId(String auctionId) {
        this.auctionId = auctionId;
    }
    
    public LocalDateTime getNewEndTime() {
        return newEndTime;
    }
    
    public void setNewEndTime(LocalDateTime newEndTime) {
        this.newEndTime = newEndTime;
    }
    
    public int getMinutesAdded() {
        return minutesAdded;
    }
    
    public void setMinutesAdded(int minutesAdded) {
        this.minutesAdded = minutesAdded;
    }
}
