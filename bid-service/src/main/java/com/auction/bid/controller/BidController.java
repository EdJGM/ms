package com.auction.bid.controller;

import com.auction.bid.dto.BidRequest;
import com.auction.bid.model.Bid;
import com.auction.bid.service.BidService;
import com.auction.bid.security.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/auctions")
public class BidController {
    private final BidService bidService;
    private final JwtUtils jwtUtils;

    @Autowired
    public BidController(BidService bidService, JwtUtils jwtUtils) {
        this.bidService = bidService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/{auctionId}/bids")
    public ResponseEntity<Bid> createBid(@PathVariable Long auctionId, @RequestParam Long userId, @RequestBody BidRequest bidRequest, HttpServletRequest request) {
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

        Bid bid = bidService.createBid(bidRequest, auctionId, userId, username);
        return new ResponseEntity<>(bid, HttpStatus.CREATED);
    }
    @DeleteMapping("/{auctionId}/bids/{bidId}")
    public ResponseEntity<String> deleteBid(@PathVariable Long auctionId, @PathVariable Long bidId, @RequestParam Long userId) {
        bidService.deleteBid(auctionId, bidId, userId);
        return ResponseEntity.ok("Bid deleted successfully");
    }
    @GetMapping("/{auctionId}/bids")
    public ResponseEntity<List<Bid>> getBidsForAuction(@PathVariable Long auctionId) {
        List<Bid> bids = bidService.getBidsForAuction(auctionId);
        return new ResponseEntity<>(bids, HttpStatus.OK);
    }
    @GetMapping("/{auctionId}/bids/{bidId}")
    public ResponseEntity<Bid> getBidById(@PathVariable Long auctionId, @PathVariable Long bidId) {
        Bid bid = bidService.getBidById(auctionId, bidId);
        if (bid != null) {
            return new ResponseEntity<>(bid, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/{auctionId}/bids/highest")
    public ResponseEntity<Bid> getHighestBidForAuction(@PathVariable Long auctionId) {
        Bid highestBid = bidService.getHighestBidForAuction(auctionId);
        if (highestBid != null) {
            return new ResponseEntity<>(highestBid, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
