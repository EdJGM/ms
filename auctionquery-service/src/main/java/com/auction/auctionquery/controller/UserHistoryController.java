package com.auction.auctionquery.controller;

import com.auction.auctionquery.model.UserHistory;
import com.auction.auctionquery.service.UserHistoryService;
import com.auction.auctionquery.security.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios/me")
public class UserHistoryController {

    private final UserHistoryService userHistoryService;
    private final JwtUtils jwtUtils;

    public UserHistoryController(UserHistoryService userHistoryService, JwtUtils jwtUtils) {
        this.userHistoryService = userHistoryService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping("/historial")
    public ResponseEntity<UserHistory> getUserHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            HttpServletRequest request) {

        String token = extractToken(request);
        if (!jwtUtils.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = jwtUtils.getUserNameFromJwtToken(token);
        UserHistory history = userHistoryService.getUserHistory(username, page, limit);
        return ResponseEntity.ok(history);
    }

    private String extractToken(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}