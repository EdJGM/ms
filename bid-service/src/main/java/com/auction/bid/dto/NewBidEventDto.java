package com.auction.bid.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class NewBidEventDto {
    private String auctionId;
    private BigDecimal newPrice;
    private String bidderUsername;
    private LocalDateTime timestamp;
    
    // Constructor por defecto
    public NewBidEventDto() {}
    
    // Constructor con parámetros
    public NewBidEventDto(String auctionId, BigDecimal newPrice, String bidderUsername) {
        this.auctionId = auctionId;
        this.newPrice = newPrice;
        this.bidderUsername = bidderUsername;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters y setters
    public String getAuctionId() {
        return auctionId;
    }
    
    public void setAuctionId(String auctionId) {
        this.auctionId = auctionId;
    }
    
    public BigDecimal getNewPrice() {
        return newPrice;
    }
    
    public void setNewPrice(BigDecimal newPrice) {
        this.newPrice = newPrice;
    }
    
    public String getBidderUsername() {
        return bidderUsername;
    }
    
    public void setBidderUsername(String bidderUsername) {
        this.bidderUsername = bidderUsername;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
