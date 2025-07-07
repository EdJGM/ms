package com.auction.auction.controller;

import com.auction.auction.dto.AuctionRequest;
import com.auction.auction.model.Auction;
import com.auction.auction.service.AuctionService;
import com.auction.auction.security.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/auctions")
public class AuctionController {
    private final AuctionService auctionService;
    private final JwtUtils jwtUtils;

    @Autowired
    public AuctionController(AuctionService auctionService, JwtUtils jwtUtils) {
        this.auctionService = auctionService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping
    public ResponseEntity<Auction> createAuction(@RequestBody AuctionRequest auctionRequest, HttpServletRequest request) {
        // Extraer JWT del header Authorization
        String headerAuth = request.getHeader("Authorization");
        String token = null;
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            token = headerAuth.substring(7);
        }
        if (token == null || !jwtUtils.validateJwtToken(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String username = jwtUtils.getUserNameFromJwtToken(token);

        // Ejemplo: Si quieres extraer roles del JWT, puedes hacerlo así (si los roles están en los claims):
        // Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        // List<String> roles = claims.get("roles", List.class);

        Auction auction = auctionService.createAuction(auctionRequest, username);
        return new ResponseEntity<>(auction, HttpStatus.CREATED);
    }
    @GetMapping
    public ResponseEntity<List<Auction>> getAllAuctions() {
        List<Auction> auctions = auctionService.getAllAuctions();
        return new ResponseEntity<>(auctions, HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Auction> getAuctionById(@PathVariable Long id) {
        Auction auction = auctionService.getAuctionById(id);
        if (auction != null) {
            return new ResponseEntity<>(auction, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<Auction> updateAuction(@PathVariable Long id, @RequestBody AuctionRequest auctionRequest) {
        Auction updated = auctionService.updateAuction(id, auctionRequest);
        if (updated != null) {
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuction(@PathVariable Long id) {
        auctionService.deleteAuction(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
