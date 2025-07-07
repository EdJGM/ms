package com.auction.bid.service;

import com.auction.bid.dto.BidRequest;
import com.auction.bid.model.Bid;
import com.auction.bid.repository.BidRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BidServiceImpl implements BidService {
    private final BidRepository bidRepository;
    public BidServiceImpl(BidRepository bidRepository) {
        this.bidRepository = bidRepository;
    }

    @Override
    public Bid createBid(BidRequest bidRequest, Long auctionId, Long userId, String username) {
        Bid bid = new Bid();
        bid.setAuctionId(auctionId);
        bid.setUserId(userId);
        bid.setUsername(username);
        bid.setBidPrice(bidRequest.getBidPrice());
        return bidRepository.save(bid);
    }

    @Override
    public void deleteBid(Long auctionId, Long bidId, Long userId) {
        // Solo elimina si el bid pertenece al usuario y subasta
        bidRepository.findById(bidId).ifPresent(bid -> {
            if (bid.getAuctionId().equals(auctionId) && bid.getUserId().equals(userId)) {
                bidRepository.deleteById(bidId);
            }
        });
    }

    @Override
    public List<Bid> getBidsForAuction(Long auctionId) {
        return bidRepository.findByAuctionId(auctionId);
    }

    @Override
    public Bid getBidById(Long auctionId, Long bidId) {
        Optional<Bid> bid = bidRepository.findById(bidId);
        return bid.filter(b -> b.getAuctionId().equals(auctionId)).orElse(null);
    }

    //Obtener la puja más alta de una subasta
    @Override
    public Bid getHighestBidForAuction(Long auctionId) {
        List<Bid> bids = bidRepository.findByAuctionId(auctionId);
        if (bids.isEmpty()) {
            return null; // No hay pujas para esta subasta
        }
        Bid highestBid = bids.get(0);
        for (Bid bid : bids) {
            if (bid.getBidPrice().compareTo(highestBid.getBidPrice())>0) {
                highestBid = bid;
            }
        }
        return highestBid;
    }

    // Obtener pujas de un usuario específico
    @Override
    public List<Bid> getAllBidsForUserId(Long userId) {
        List<Bid> allBids = bidRepository.findAll();
        List<Bid> userBids = new ArrayList<>();
        for (Bid bid : allBids) {
            if (bid.getUserId().equals(userId)) {
                userBids.add(bid);
            }
        }
        return userBids;
    }
}
