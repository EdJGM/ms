package com.auction.bid.service;

import com.auction.bid.dto.BidRequest;
import com.auction.bid.model.Bid;
import java.util.List;

public interface BidService {
    Bid createBid(BidRequest bidRequest, Long auctionId, Long userId, String username);
    void deleteBid(Long auctionId, Long bidId, Long userId);
    List<Bid> getBidsForAuction(Long auctionId);
    Bid getBidById(Long auctionId, Long bidId);
    Bid getHighestBidForAuction(Long auctionId);
    List<Bid> getAllBidsForUserId(Long userId);
}
