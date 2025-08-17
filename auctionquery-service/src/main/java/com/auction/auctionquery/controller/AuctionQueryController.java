package com.auction.auctionquery.controller;

import com.auction.auctionquery.model.AuctionView;
import com.auction.auctionquery.service.AuctionQueryService;
import com.auction.auctionquery.security.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/subastas")
public class AuctionQueryController {

    private final AuctionQueryService auctionQueryService;
    private final JwtUtils jwtUtils;

    public AuctionQueryController(AuctionQueryService auctionQueryService, JwtUtils jwtUtils) {
        this.auctionQueryService = auctionQueryService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping
    public ResponseEntity<List<AuctionView>> getActiveAuctions(
            @RequestParam(required = false) String categoria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            HttpServletRequest request) {

        String token = extractToken(request);
        if (!jwtUtils.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<AuctionView> auctions = auctionQueryService.getActiveAuctions(categoria, page, limit);
        return ResponseEntity.ok(auctions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuctionView> getAuctionById(@PathVariable Long id, HttpServletRequest request) {
        String token = extractToken(request);
        if (!jwtUtils.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        AuctionView auction = auctionQueryService.getAuctionById(id);
        if (auction != null && "activa".equals(auction.getEstado())) {
            return ResponseEntity.ok(auction);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<AuctionView>> searchAuctions(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String categoria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            HttpServletRequest request) {

        String token = extractToken(request);
        if (!jwtUtils.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<AuctionView> auctions = auctionQueryService.searchAuctions(searchTerm, categoria, page, limit);
        return ResponseEntity.ok(auctions);
    }

    private String extractToken(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}