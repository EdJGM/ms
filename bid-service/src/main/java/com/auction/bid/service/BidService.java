package com.auction.bid.service;

import com.auction.bid.dto.BidRequest;
import com.auction.bid.model.Bid;
import java.util.List;

public interface BidService {
    // Métodos existentes
    Bid createBid(BidRequest bidRequest, Long auctionId, Long userId, String username);
    void deleteBid(Long auctionId, Long bidId, Long userId);
    List<Bid> getBidsForAuction(Long auctionId);
    Bid getBidById(Long auctionId, Long bidId);
    Bid getHighestBidForAuction(Long auctionId);
    List<Bid> getAllBidsForUserId(Long userId);
    boolean isAuctionActive(Long auctionId);
    Bid createBid(BidRequest bidRequest, Long auctionId, String username);

    // ✅ NUEVOS MÉTODOS: Validaciones de negocio
    Bid createBidWithValidation(BidRequest bidRequest, Long auctionId, String username, String token);
    void validateBidRules(BidRequest bidRequest, Long auctionId, String token);
    boolean canUserBid(Long auctionId, String username, String token);
}