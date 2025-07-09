package com.auction.auctionquery.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BidHistory {
    private Long auctionId;
    private String auctionTitle;
    private String auctionDescription;
    private BigDecimal bidAmount;
    private LocalDateTime bidTime;
    private boolean winningBid;

    // Constructors
    public BidHistory() {}

    // Getters and setters
    public Long getAuctionId() { return auctionId; }
    public void setAuctionId(Long auctionId) { this.auctionId = auctionId; }

    public String getAuctionTitle() { return auctionTitle; }
    public void setAuctionTitle(String auctionTitle) { this.auctionTitle = auctionTitle; }

    public String getAuctionDescription() { return auctionDescription; }
    public void setAuctionDescription(String auctionDescription) { this.auctionDescription = auctionDescription; }

    public BigDecimal getBidAmount() { return bidAmount; }
    public void setBidAmount(BigDecimal bidAmount) { this.bidAmount = bidAmount; }

    public LocalDateTime getBidTime() { return bidTime; }
    public void setBidTime(LocalDateTime bidTime) { this.bidTime = bidTime; }

    public boolean isWinningBid() { return winningBid; }
    public void setWinningBid(boolean winningBid) { this.winningBid = winningBid; }
}