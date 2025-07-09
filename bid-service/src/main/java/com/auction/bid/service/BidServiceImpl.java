package com.auction.bid.service;

import com.auction.bid.client.AuctionServiceClient;
import com.auction.bid.client.NotificationServiceClient;
import com.auction.bid.client.UserServiceClient;
import com.auction.bid.dto.AuctionDto;
import com.auction.bid.dto.BidRequest;
import com.auction.bid.dto.NewBidEventDto;
import com.auction.bid.exception.BusinessRuleException;
import com.auction.bid.exception.ResourceNotFoundException;
import com.auction.bid.model.Bid;
import com.auction.bid.repository.BidRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BidServiceImpl implements BidService {
    private final BidRepository bidRepository;

    @Autowired
    private AuctionServiceClient auctionServiceClient;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private NotificationServiceClient notificationServiceClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BidValidationService bidValidationService;

    public BidServiceImpl(BidRepository bidRepository) {
        this.bidRepository = bidRepository;
    }

    @Override
    public Bid createBidWithValidation(BidRequest bidRequest, Long auctionId, String username, String token) {
        try {
            // 1. Ejecutar todas las validaciones de negocio
            bidValidationService.validateBid(bidRequest, auctionId, token);

            // 2. Verificar que el usuario puede pujar
            if (!canUserBid(auctionId, username, token)) {
                throw new BusinessRuleException("El usuario no puede pujar en esta subasta");
            }

            // 3. Obtener información del usuario
            String userResponse = userServiceClient.getUserByUsername(username, token);
            JsonNode userNode = objectMapper.readTree(userResponse);
            Long userId = userNode.get("id").asLong();

            // 4. Crear la puja
            Bid savedBid = createBid(bidRequest, auctionId, userId, username);

            // 5. Actualizar precio actual en auction-service (si es necesario)
            // Esto podría requerir un endpoint adicional en auction-service

            // 6. Notificar al servicio de notificaciones WebSocket
            try {
                NewBidEventDto eventDto = new NewBidEventDto(
                        auctionId.toString(),
                        savedBid.getBidPrice(),
                        savedBid.getUsername()
                );
                notificationServiceClient.broadcastNewBid(eventDto, token);
            } catch (Exception e) {
                // Log pero no fallar - la puja ya se guardó
                System.err.println("Error notifying new bid: " + e.getMessage());
            }

            return savedBid;

        } catch (BusinessRuleException | ResourceNotFoundException e) {
            // Re-lanzar excepciones de negocio
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar la puja: " + e.getMessage(), e);
        }
    }

    //Validar reglas de negocio
    @Override
    public void validateBidRules(BidRequest bidRequest, Long auctionId, String token) {
        bidValidationService.validateBid(bidRequest, auctionId, token);
    }

    //Verificar si un usuario puede pujar
    @Override
    public boolean canUserBid(Long auctionId, String username, String token) {
        try {
            AuctionDto auction = bidValidationService.getAuctionInfo(auctionId, token);

            // El dueño de la subasta no puede pujar en su propia subasta
            if (auction.getOwnerUsername() != null && auction.getOwnerUsername().equals(username)) {
                return false;
            }

            // Verificar que la subasta esté activa
            return bidValidationService.isAuctionActive(auctionId, token);

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Bid createBid(BidRequest bidRequest, Long auctionId, String username) {
        String token = getAuthorizationToken();
        return createBidWithValidation(bidRequest, auctionId, username, token);
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

    @Override
    public boolean isAuctionActive(Long auctionId) {
        try {
            String token = getAuthorizationToken();
            Boolean isActive = auctionServiceClient.isAuctionActive(auctionId, token);
            return isActive != null ? isActive : false;
        } catch (Exception e) {
            // Si hay error en la comunicación, asumir que la subasta no está activa
            return false;
        }
    }

    private String getAuthorizationToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String authHeader = request.getHeader("Authorization");
            return authHeader != null ? authHeader : "";
        }
        return "";
    }
}
