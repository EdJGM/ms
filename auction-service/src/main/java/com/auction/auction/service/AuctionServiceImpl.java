package com.auction.auction.service;

import com.auction.auction.client.NotificationServiceClient;
import com.auction.auction.dto.AuctionRequest;
import com.auction.auction.dto.AuctionExtendedEventDto;
import com.auction.auction.dto.ModeratorJoinedEventDto;
import com.auction.auction.dto.AuctionFinishedEventDto;
import com.auction.auction.model.Auction;
import com.auction.auction.repository.AuctionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AuctionServiceImpl implements AuctionService {
    private final AuctionRepository auctionRepository;
    
    @Autowired
    private NotificationServiceClient notificationServiceClient;

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

    @Override
    public Auction startAuction(Long id, String ownerUsername) {
        Optional<Auction> auctionOpt = auctionRepository.findById(id);
        if (auctionOpt.isPresent()) {
            Auction auction = auctionOpt.get();
            if (auction.getOwnerUsername().equals(ownerUsername)) {
                auction.setEstado("activa");
                return auctionRepository.save(auction);
            }
        }
        return null;
    }

    @Override
    public Auction endAuction(Long id, String ownerUsername) {
        Optional<Auction> auctionOpt = auctionRepository.findById(id);
        if (auctionOpt.isPresent()) {
            Auction auction = auctionOpt.get();
            if (auction.getOwnerUsername().equals(ownerUsername)) {
                auction.setEstado("finalizada");
                return auctionRepository.save(auction);
            }
        }
        return null;
    }

    @Override
    public Auction extendAuction(Long id, int minutes, String ownerUsername) {
        Optional<Auction> auctionOpt = auctionRepository.findById(id);
        if (auctionOpt.isPresent()) {
            Auction auction = auctionOpt.get();
            if (auction.getOwnerUsername().equals(ownerUsername)) {
                LocalDateTime newEndTime = LocalDateTime.now().plusMinutes(minutes);
                auction.setDaysToEndTime(auction.getDaysToEndTime() + (minutes / 1440)); // Convert minutes to days
                
                Auction savedAuction = auctionRepository.save(auction);
                
                // Notificar extensión
                try {
                    String token = getAuthorizationToken();
                    AuctionExtendedEventDto eventDto = new AuctionExtendedEventDto();
                    eventDto.setAuctionId(id.toString());
                    eventDto.setNewEndTime(newEndTime);
                    eventDto.setMinutesAdded(minutes);
                    
                    notificationServiceClient.broadcastAuctionExtended(eventDto, token);
                } catch (Exception e) {
                    System.err.println("Error notifying auction extension: " + e.getMessage());
                }
                
                return savedAuction;
            }
        }
        return null;
    }

    @Override
    public boolean joinModerationSession(Long id, String ownerUsername) {
        Optional<Auction> auctionOpt = auctionRepository.findById(id);
        if (auctionOpt.isPresent()) {
            Auction auction = auctionOpt.get();
            if (auction.getOwnerUsername().equals(ownerUsername)) {
                // Notificar que el moderador se unió
                try {
                    String token = getAuthorizationToken();
                    ModeratorJoinedEventDto eventDto = new ModeratorJoinedEventDto();
                    eventDto.setAuctionId(id.toString());
                    eventDto.setModeratorName(ownerUsername);
                    eventDto.setJoinTime(LocalDateTime.now());
                    
                    notificationServiceClient.broadcastModeratorJoined(eventDto, token);
                } catch (Exception e) {
                    System.err.println("Error notifying moderator join: " + e.getMessage());
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean leaveModerationSession(Long id, String ownerUsername) {
        Optional<Auction> auctionOpt = auctionRepository.findById(id);
        if (auctionOpt.isPresent()) {
            Auction auction = auctionOpt.get();
            return auction.getOwnerUsername().equals(ownerUsername);
        }
        return false;
    }
    
    private String getAuthorizationToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            return request.getHeader("Authorization");
        }
        return "";
    }
}
