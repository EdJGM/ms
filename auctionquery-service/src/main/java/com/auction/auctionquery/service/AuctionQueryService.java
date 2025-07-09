package com.auction.auctionquery.service;

import com.auction.auctionquery.model.AuctionView;
import java.util.List;

public interface AuctionQueryService {
    List<AuctionView> getActiveAuctions(String categoria, int page, int limit);
    AuctionView getAuctionById(Long id);
    List<AuctionView> searchAuctions(String searchTerm, String categoria, int page, int limit);
}
