package com.auction.auctionquery.model;

import java.math.BigDecimal;
import java.util.List;

public class UserHistory {
    private String username;
    private int totalAuctionsParticipated;
    private int totalAuctionsWon;
    private BigDecimal totalAmountSpent;
    private List<BidHistory> bidHistory;
    private List<AuctionResult> auctionResults;

    // Constructors
    public UserHistory() {}

    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getTotalAuctionsParticipated() { return totalAuctionsParticipated; }
    public void setTotalAuctionsParticipated(int totalAuctionsParticipated) { this.totalAuctionsParticipated = totalAuctionsParticipated; }

    public int getTotalAuctionsWon() { return totalAuctionsWon; }
    public void setTotalAuctionsWon(int totalAuctionsWon) { this.totalAuctionsWon = totalAuctionsWon; }

    public BigDecimal getTotalAmountSpent() { return totalAmountSpent; }
    public void setTotalAmountSpent(BigDecimal totalAmountSpent) { this.totalAmountSpent = totalAmountSpent; }

    public List<BidHistory> getBidHistory() { return bidHistory; }
    public void setBidHistory(List<BidHistory> bidHistory) { this.bidHistory = bidHistory; }

    public List<AuctionResult> getAuctionResults() { return auctionResults; }
    public void setAuctionResults(List<AuctionResult> auctionResults) { this.auctionResults = auctionResults; }
}