package com.auction.auction.service;

import com.auction.auction.dto.AuctionRequest;
import com.auction.auction.model.Auction;
import java.util.List;

public interface AuctionService {
    Auction createAuction(AuctionRequest auctionRequest, String ownerUsername);
    List<Auction> getAllAuctions();
    void deleteAuction(Long id);
    Auction updateAuction(Long id, AuctionRequest auctionRequest);
    Auction getAuctionById(Long id);
    Auction startAuction(Long id, String ownerUsername);
    Auction endAuction(Long id, String ownerUsername);
    Auction extendAuction(Long id, int minutes, String ownerUsername);
    boolean joinModerationSession(Long id, String ownerUsername);
    boolean leaveModerationSession(Long id, String ownerUsername);
}
