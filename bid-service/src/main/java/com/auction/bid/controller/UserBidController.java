package com.auction.bid.controller;

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
@RequestMapping("/usuarios")
public class UserBidController {
    private final BidService bidService;
    private final JwtUtils jwtUtils;

    @Autowired
    public UserBidController(BidService bidService, JwtUtils jwtUtils) {
        this.bidService = bidService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping("/{userId}/pujas")
    public ResponseEntity<List<Bid>> getAllBidsForUser(@PathVariable Long userId, HttpServletRequest request) {
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

        List<Bid> bids = bidService.getAllBidsForUserId(userId);
        return new ResponseEntity<>(bids, HttpStatus.OK);
    }
}
