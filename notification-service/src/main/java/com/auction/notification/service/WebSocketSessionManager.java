package com.auction.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
public class WebSocketSessionManager {

    @Autowired
    private ObjectMapper objectMapper;

    // Mapa de sesiones por sala de subasta
    private final Map<String, CopyOnWriteArraySet<WebSocketSession>> auctionRooms = new ConcurrentHashMap<>();

    // Mapa de sesión a sala actual
    private final Map<WebSocketSession, String> sessionToRoom = new ConcurrentHashMap<>();

    public void addSession(WebSocketSession session) {
        // Sesión autenticada agregada
        System.out.println("Session added: " + session.getId());
    }

    public void removeSession(WebSocketSession session) {
        String currentRoom = sessionToRoom.remove(session);
        if (currentRoom != null) {
            CopyOnWriteArraySet<WebSocketSession> roomSessions = auctionRooms.get(currentRoom);
            if (roomSessions != null) {
                roomSessions.remove(session);
                if (roomSessions.isEmpty()) {
                    auctionRooms.remove(currentRoom);
                }
            }
        }
    }

    public void joinAuctionRoom(WebSocketSession session, String auctionId) {
        // Salir de la sala actual si existe
        leaveAuctionRoom(session);

        // Unirse a la nueva sala
        auctionRooms.computeIfAbsent(auctionId, k -> new CopyOnWriteArraySet<>()).add(session);
        sessionToRoom.put(session, auctionId);

        System.out.println("User joined auction room: " + auctionId);
    }

    public void leaveAuctionRoom(WebSocketSession session) {
        String currentRoom = sessionToRoom.remove(session);
        if (currentRoom != null) {
            CopyOnWriteArraySet<WebSocketSession> roomSessions = auctionRooms.get(currentRoom);
            if (roomSessions != null) {
                roomSessions.remove(session);
                if (roomSessions.isEmpty()) {
                    auctionRooms.remove(currentRoom);
                }
            }
        }
    }

    // Métodos para enviar eventos específicos
    public void sendNewBid(String auctionId, Map<String, Object> bidData) {
        sendToAuctionRoom(auctionId, "nueva_puja", bidData);
    }

    public void sendTimeUpdate(String auctionId, Map<String, Object> timeData) {
        sendToAuctionRoom(auctionId, "tiempo_actualizado", timeData);
    }

    public void sendAuctionFinished(String auctionId, Map<String, Object> finishData) {
        sendToAuctionRoom(auctionId, "subasta_finalizada", finishData);
    }

    public void sendModeratorJoined(String auctionId, Map<String, Object> moderatorData) {
        sendToAuctionRoom(auctionId, "moderador_ingreso", moderatorData);
    }

    public void sendAuctionExtended(String auctionId, Map<String, Object> extensionData) {
        sendToAuctionRoom(auctionId, "subasta_extendida", extensionData);
    }

    private void sendToAuctionRoom(String auctionId, String event, Map<String, Object> data) {
        CopyOnWriteArraySet<WebSocketSession> roomSessions = auctionRooms.get(auctionId);
        if (roomSessions != null) {
            Map<String, Object> message = Map.of(
                    "event", event,
                    "data", data
            );

            String messageJson;
            try {
                messageJson = objectMapper.writeValueAsString(message);
            } catch (Exception e) {
                System.err.println("Error serializing message: " + e.getMessage());
                return;
            }

            roomSessions.removeIf(session -> {
                try {
                    if (session.isOpen()) {
                        session.sendMessage(new TextMessage(messageJson));
                        return false;
                    } else {
                        return true; // Remover sesión cerrada
                    }
                } catch (IOException e) {
                    System.err.println("Error sending message to session: " + e.getMessage());
                    return true; // Remover sesión problemática
                }
            });
        }
    }

    public int getActiveSessionsCount() {
        return sessionToRoom.size();
    }

    public int getAuctionRoomCount() {
        return auctionRooms.size();
    }
}