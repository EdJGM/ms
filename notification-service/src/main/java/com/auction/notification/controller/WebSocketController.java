package com.auction.notification.controller;

import com.auction.notification.dto.NewBidEventDto;
import com.auction.notification.dto.AuctionExtendedEventDto;
import com.auction.notification.dto.ModeratorJoinedEventDto;
import com.auction.notification.security.JwtUtils;
import com.auction.notification.service.WebSocketSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/ws")
public class WebSocketController {

    @Autowired
    private WebSocketSessionManager sessionManager;
    
    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/broadcast/new-bid")
    public ResponseEntity<?> broadcastNewBid(@RequestBody NewBidEventDto eventDto, 
                                           HttpServletRequest request) {
        
        // Validar autenticación
        String token = extractToken(request);
        if (token == null || !jwtUtils.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        // Convertir DTO a formato WebSocket
        Map<String, Object> bidData = Map.of(
            "subastaId", eventDto.getAuctionId(),
            "nuevoPrecio", eventDto.getNewPrice(),
            "nombrePujador", eventDto.getBidderUsername(),
            "timestamp", eventDto.getTimestamp()
        );
        
        sessionManager.sendNewBid(eventDto.getAuctionId(), bidData);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/broadcast/time-update")
    @SuppressWarnings("unchecked")
    public void broadcastTimeUpdate(@RequestBody Map<String, Object> request) {
        String auctionId = (String) request.get("auctionId");
        Map<String, Object> timeData = (Map<String, Object>) request.get("timeData");
        sessionManager.sendTimeUpdate(auctionId, timeData);
    }

    @PostMapping("/broadcast/auction-finished")
    @SuppressWarnings("unchecked")
    public void broadcastAuctionFinished(@RequestBody Map<String, Object> request) {
        String auctionId = (String) request.get("auctionId");
        Map<String, Object> finishData = (Map<String, Object>) request.get("finishData");
        sessionManager.sendAuctionFinished(auctionId, finishData);
    }

    @PostMapping("/broadcast/auction-extended")
    public ResponseEntity<?> broadcastAuctionExtended(@RequestBody AuctionExtendedEventDto eventDto,
                                                     HttpServletRequest request) {
        
        String token = extractToken(request);
        if (token == null || !jwtUtils.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Map<String, Object> extensionData = Map.of(
            "subastaId", eventDto.getAuctionId(),
            "nuevaHoraFin", eventDto.getNewEndTime().toString(),
            "minutosAñadidos", eventDto.getMinutesAdded()
        );
        
        sessionManager.sendAuctionExtended(eventDto.getAuctionId(), extensionData);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/broadcast/moderator-joined")
    public ResponseEntity<?> broadcastModeratorJoined(@RequestBody ModeratorJoinedEventDto eventDto,
                                                     HttpServletRequest request) {
        
        String token = extractToken(request);
        if (token == null || !jwtUtils.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Map<String, Object> moderatorData = Map.of(
            "subastaId", eventDto.getAuctionId(),
            "nombreModerador", eventDto.getModeratorName(),
            "horaIngreso", eventDto.getJoinTime()
        );
        
        sessionManager.sendModeratorJoined(eventDto.getAuctionId(), moderatorData);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats")
    public Map<String, Object> getWebSocketStats() {
        return Map.of(
                "activeSessions", sessionManager.getActiveSessionsCount(),
                "activeRooms", sessionManager.getAuctionRoomCount()
        );
    }
    
    private String extractToken(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}