package com.auction.bid.dto;

import java.math.BigDecimal;

public class BidRequest {
    private BigDecimal bidPrice;
    public BidRequest() {}
    public BidRequest(BigDecimal bidPrice) { this.bidPrice = bidPrice; }
    public BigDecimal getBidPrice() { return bidPrice; }
    public void setBidPrice(BigDecimal bidPrice) { this.bidPrice = bidPrice; }
}
