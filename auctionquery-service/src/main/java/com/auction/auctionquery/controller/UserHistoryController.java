package com.auction.auctionquery.controller;

import com.auction.auctionquery.model.UserHistory;
import com.auction.auctionquery.service.UserHistoryService;
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

    public UserHistoryController(UserHistoryService userHistoryService) {
        this.userHistoryService = userHistoryService;
    }

    @GetMapping("/historial")
    public ResponseEntity<UserHistory> getUserHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            HttpServletRequest request) {

        // 🔄 NUEVO: Usar las cabeceras del API Gateway
        String username = request.getHeader("X-User-Id");
        String userRole = request.getHeader("X-User-Role");

        if (username == null || userRole == null) {
            System.out.println("🔍 [AUCTION-QUERY] Headers missing - X-User-Id: " + username + ", X-User-Role: " + userRole);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        System.out.println("🔍 [AUCTION-QUERY] Username from header: " + username);
        System.out.println("🔍 [AUCTION-QUERY] Role from header: " + userRole);

        UserHistory history = userHistoryService.getUserHistory(username, page, limit);
        return ResponseEntity.ok(history);
    }
}