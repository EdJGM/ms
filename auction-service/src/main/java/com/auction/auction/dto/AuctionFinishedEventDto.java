package com.auction.auction.dto;

import java.time.LocalDateTime;

public class AuctionFinishedEventDto {
    private String auctionId;
    private String winnerUsername;
    private LocalDateTime finishTime;
    
    // Constructor por defecto
    public AuctionFinishedEventDto() {}
    
    // Constructor con parámetros
    public AuctionFinishedEventDto(String auctionId, String winnerUsername, LocalDateTime finishTime) {
        this.auctionId = auctionId;
        this.winnerUsername = winnerUsername;
        this.finishTime = finishTime;
    }
    
    // Getters y setters
    public String getAuctionId() {
        return auctionId;
    }
    
    public void setAuctionId(String auctionId) {
        this.auctionId = auctionId;
    }
    
    public String getWinnerUsername() {
        return winnerUsername;
    }
    
    public void setWinnerUsername(String winnerUsername) {
        this.winnerUsername = winnerUsername;
    }
    
    public LocalDateTime getFinishTime() {
        return finishTime;
    }
    
    public void setFinishTime(LocalDateTime finishTime) {
        this.finishTime = finishTime;
    }
}
