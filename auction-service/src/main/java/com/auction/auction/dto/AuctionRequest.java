package com.auction.auction.dto;

import java.math.BigDecimal;

public class AuctionRequest {
    private String description;
    private BigDecimal startingPrice;
    private String itemStatus;
    private String itemCategory;
    private int daysToEndTime;
    // Getters and setters
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getStartingPrice() { return startingPrice; }
    public void setStartingPrice(BigDecimal startingPrice) { this.startingPrice = startingPrice; }
    public String getItemStatus() { return itemStatus; }
    public void setItemStatus(String itemStatus) { this.itemStatus = itemStatus; }
    public String getItemCategory() { return itemCategory; }
    public void setItemCategory(String itemCategory) { this.itemCategory = itemCategory; }
    public int getDaysToEndTime() { return daysToEndTime; }
    public void setDaysToEndTime(int daysToEndTime) { this.daysToEndTime = daysToEndTime; }
}
