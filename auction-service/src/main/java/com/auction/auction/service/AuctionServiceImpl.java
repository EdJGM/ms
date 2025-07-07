package com.auction.auction.service;

import com.auction.auction.dto.AuctionRequest;
import com.auction.auction.model.Auction;
import com.auction.auction.repository.AuctionRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AuctionServiceImpl implements AuctionService {
    private final AuctionRepository auctionRepository;

    public AuctionServiceImpl(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
    }
    @Override
    public Auction createAuction(AuctionRequest auctionRequest, String ownerUsername) {
        Auction auction = new Auction();
        auction.setDescription(auctionRequest.getDescription());
        auction.setStartingPrice(auctionRequest.getStartingPrice());
        auction.setItemStatus(auctionRequest.getItemStatus());
        auction.setItemCategory(auctionRequest.getItemCategory());
        auction.setDaysToEndTime(auctionRequest.getDaysToEndTime());
        auction.setOwnerUsername(ownerUsername);
        return auctionRepository.save(auction);
    }
    @Override
    public List<Auction> getAllAuctions() {
        return auctionRepository.findAll();
    }
    @Override
    public void deleteAuction(Long id) {
        auctionRepository.deleteById(id);
    }
    @Override
    public Auction updateAuction(Long id, AuctionRequest auctionRequest) {
        Optional<Auction> auctionOpt = auctionRepository.findById(id);
        if (auctionOpt.isPresent()) {
            Auction auction = auctionOpt.get();
            auction.setDescription(auctionRequest.getDescription());
            auction.setStartingPrice(auctionRequest.getStartingPrice());
            auction.setItemStatus(auctionRequest.getItemStatus());
            auction.setItemCategory(auctionRequest.getItemCategory());
            auction.setDaysToEndTime(auctionRequest.getDaysToEndTime());
            return auctionRepository.save(auction);
        }
        return null;
    }
    @Override
    public Auction getAuctionById(Long id) {
        return auctionRepository.findById(id).orElse(null);
    }
}
