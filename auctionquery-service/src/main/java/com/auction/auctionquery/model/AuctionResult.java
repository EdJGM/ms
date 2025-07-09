package com.auction.auctionquery.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AuctionResult {
    private Long auctionId;
    private String auctionDescription;
    private BigDecimal finalPrice;
    private LocalDateTime endTime;
    private boolean won;

    // Getters and setters
    public Long getAuctionId() { return auctionId; }
    public void setAuctionId(Long auctionId) { this.auctionId = auctionId; }
    public String getAuctionDescription() { return auctionDescription; }
    public void setAuctionDescription(String auctionDescription) { this.auctionDescription = auctionDescription; }
    public BigDecimal getFinalPrice() { return finalPrice; }
    public void setFinalPrice(BigDecimal finalPrice) { this.finalPrice = finalPrice; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public boolean isWon() { return won; }
    public void setWon(boolean won) { this.won = won; }
}