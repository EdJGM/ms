package com.auction.notification.handler;

import com.auction.notification.security.JwtUtils;
import com.auction.notification.service.WebSocketSessionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.Map;

@Component
public class AuctionWebSocketHandler implements WebSocketHandler {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private WebSocketSessionManager sessionManager;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String token = extractTokenFromSession(session);
        if (token != null && jwtUtils.validateJwtToken(token)) {
            String username = jwtUtils.getUserNameFromJwtToken(token);
            session.getAttributes().put("username", username);
            session.getAttributes().put("authenticated", true);
            sessionManager.addSession(session);
            System.out.println("WebSocket connection established for user: " + username);
        } else {
            sendErrorMessage(session, "Invalid token");
            session.close(CloseStatus.NOT_ACCEPTABLE);
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        if (message instanceof TextMessage) {
            String payload = ((TextMessage) message).getPayload();
            handleTextMessage(session, payload);
        }
    }

    private void handleTextMessage(WebSocketSession session, String payload) {
        try {
            Map<String, Object> messageData = objectMapper.readValue(payload, Map.class);
            String action = (String) messageData.get("action");

            switch (action) {
                case "join_auction":
                    String auctionId = (String) messageData.get("auctionId");
                    sessionManager.joinAuctionRoom(session, auctionId);
                    break;
                case "leave_auction":
                    sessionManager.leaveAuctionRoom(session);
                    break;
                default:
                    sendErrorMessage(session, "Unknown action: " + action);
            }
        } catch (Exception e) {
            sendErrorMessage(session, "Invalid message format");
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("WebSocket transport error: " + exception.getMessage());
        sessionManager.removeSession(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        String username = (String) session.getAttributes().get("username");
        System.out.println("WebSocket connection closed for user: " + username);
        sessionManager.removeSession(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private String extractTokenFromSession(WebSocketSession session) {
        String query = session.getUri().getQuery();
        if (query != null && query.contains("token=")) {
            return query.split("token=")[1].split("&")[0];
        }
        return null;
    }

    private void sendErrorMessage(WebSocketSession session, String message) {
        try {
            Map<String, Object> errorMessage = Map.of(
                    "event", "error_notificacion",
                    "data", Map.of("codigo", "401", "mensaje", message)
            );
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(errorMessage)));
        } catch (IOException e) {
            System.err.println("Error sending error message: " + e.getMessage());
        }
    }
}