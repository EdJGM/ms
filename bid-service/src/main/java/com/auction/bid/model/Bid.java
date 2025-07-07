package com.auction.bid.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "bids")
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bidId;

    private Long auctionId;
    private Long userId;
    private String username;
    private BigDecimal bidPrice;

    // Getters y setters
    public Long getBidId() { return bidId; }
    public void setBidId(Long bidId) { this.bidId = bidId; }
    public Long getAuctionId() { return auctionId; }
    public void setAuctionId(Long auctionId) { this.auctionId = auctionId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public BigDecimal getBidPrice() { return bidPrice; }
    public void setBidPrice(BigDecimal bidPrice) { this.bidPrice = bidPrice; }
}
