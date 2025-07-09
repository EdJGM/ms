package com.auction.bid.controller;

import com.auction.bid.dto.BidRequest;
import com.auction.bid.exception.BusinessRuleException;
import com.auction.bid.exception.ResourceNotFoundException;
import com.auction.bid.model.Bid;
import com.auction.bid.service.BidService;
import com.auction.bid.security.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/subastas")
public class BidController {
    private final BidService bidService;
    private final JwtUtils jwtUtils;

    @Autowired
    public BidController(BidService bidService, JwtUtils jwtUtils) {
        this.bidService = bidService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/{id}/pujas")
    public ResponseEntity<?> createBid(@PathVariable Long id, @RequestBody BidRequest bidRequest, HttpServletRequest request) {
        try {
            String headerAuth = request.getHeader("Authorization");
            String token = null;
            if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
                token = headerAuth.substring(7);
            }
            if (token == null || !jwtUtils.validateJwtToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "UNAUTHORIZED", "message", "Token inválido o ausente"));
            }
            String username = jwtUtils.getUserNameFromJwtToken(token);

            // ✅ Usar método con validaciones completas
            Bid bid = bidService.createBidWithValidation(bidRequest, id, username, headerAuth);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Puja registrada exitosamente",
                    "bid", bid
            ));

        } catch (BusinessRuleException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "error", "BUSINESS_RULE_VIOLATION",
                    "message", e.getMessage()
            ));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "error", "RESOURCE_NOT_FOUND",
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "INTERNAL_ERROR",
                    "message", "Error interno del servidor: " + e.getMessage()
            ));
        }
    }

    //Endpoint para validar si se puede pujar (sin crear puja)
    @PostMapping("/{id}/pujas/validate")
    public ResponseEntity<?> validateBid(@PathVariable Long id, @RequestBody BidRequest bidRequest, HttpServletRequest request) {
        try {
            String headerAuth = request.getHeader("Authorization");
            String token = null;
            if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
                token = headerAuth.substring(7);
            }
            if (token == null || !jwtUtils.validateJwtToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "UNAUTHORIZED", "message", "Token inválido o ausente"));
            }
            String username = jwtUtils.getUserNameFromJwtToken(token);

            // Ejecutar solo las validaciones
            bidService.validateBidRules(bidRequest, id, headerAuth);

            // Verificar si el usuario puede pujar
            boolean canBid = bidService.canUserBid(id, username, headerAuth);

            return ResponseEntity.ok(Map.of(
                    "valid", true,
                    "canBid", canBid,
                    "message", canBid ? "La puja es válida" : "El usuario no puede pujar en esta subasta"
            ));

        } catch (BusinessRuleException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "valid", false,
                    "error", "BUSINESS_RULE_VIOLATION",
                    "message", e.getMessage()
            ));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "valid", false,
                    "error", "RESOURCE_NOT_FOUND",
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "valid", false,
                    "error", "INTERNAL_ERROR",
                    "message", "Error interno del servidor: " + e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{id}/pujas/{bidId}")
    public ResponseEntity<String> deleteBid(@PathVariable Long id, @PathVariable Long bidId, @RequestParam Long userId) {
        bidService.deleteBid(id, bidId, userId);
        return ResponseEntity.ok("Bid deleted successfully");
    }

    @GetMapping("/{id}/pujas")
    public ResponseEntity<List<Bid>> getBidsForAuction(@PathVariable Long id) {
        List<Bid> bids = bidService.getBidsForAuction(id);
        return new ResponseEntity<>(bids, HttpStatus.OK);
    }

    @GetMapping("/{id}/pujas/{bidId}")
    public ResponseEntity<Bid> getBidById(@PathVariable Long id, @PathVariable Long bidId) {
        Bid bid = bidService.getBidById(id, bidId);
        if (bid != null) {
            return new ResponseEntity<>(bid, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}/pujas/highest")
    public ResponseEntity<Bid> getHighestBidForAuction(@PathVariable Long id) {
        Bid highestBid = bidService.getHighestBidForAuction(id);
        if (highestBid != null) {
            return new ResponseEntity<>(highestBid, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
