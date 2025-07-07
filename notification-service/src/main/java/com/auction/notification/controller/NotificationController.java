package com.auction.notification.controller;

import com.auction.notification.dto.EmailRequest;
import com.auction.notification.dto.NotificationRequest;
import com.auction.notification.model.Notification;
import com.auction.notification.service.EmailSenderService;
import com.auction.notification.service.NotificationService;
import com.auction.notification.security.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final EmailSenderService emailSenderService;
    private final JwtUtils jwtUtils;
    private final NotificationService notificationService;

    @Autowired
    public NotificationController(EmailSenderService emailSenderService, JwtUtils jwtUtils, NotificationService notificationService) {
        this.emailSenderService = emailSenderService;
        this.jwtUtils = jwtUtils;
        this.notificationService = notificationService;
    }
    @GetMapping("/history")
    public ResponseEntity<?> getNotificationHistory(HttpServletRequest httpRequest) {
        String headerAuth = httpRequest.getHeader("Authorization");
        String token = null;
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            token = headerAuth.substring(7);
        }
        if (token == null || !jwtUtils.validateJwtToken(token)) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        String username = jwtUtils.getUserNameFromJwtToken(token);
        return ResponseEntity.ok(notificationService.getNotificationsByUser(username));
    }

    @PostMapping("/email")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequest request, HttpServletRequest httpRequest) {
        // Extraer JWT del header Authorization
        String headerAuth = httpRequest.getHeader("Authorization");
        String token = null;
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            token = headerAuth.substring(7);
        }
        if (token == null || !jwtUtils.validateJwtToken(token)) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        String username = jwtUtils.getUserNameFromJwtToken(token);

        // Ejemplo: Si quieres extraer roles del JWT, puedes hacerlo así (si los roles están en los claims):
        // Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        // List<String> roles = claims.get("roles", List.class);

        // Puedes usar el username para logging, auditoría, o lógica de negocio
        // Guardar el usuario que envía la notificación
        emailSenderService.sendEmailWithSender(request.getEmail(), request.getSubject(), request.getText(), username);
        return ResponseEntity.ok("Email sent successfully by user: " + username);
    }

    @PostMapping("/send")
    public ResponseEntity<Notification> sendNotificationToUser(@RequestBody NotificationRequest request, HttpServletRequest httpRequest) {
        // Extraer JWT del header Authorization
        String headerAuth = httpRequest.getHeader("Authorization");
        String token = null;
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            token = headerAuth.substring(7);
        }
        if (token == null || !jwtUtils.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = jwtUtils.getUserNameFromJwtToken(token);
        
        Notification notification = notificationService.sendNotificationToUser(request, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(notification);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsByUserId(@PathVariable Long userId, HttpServletRequest httpRequest) {
        // Extraer JWT del header Authorization
        String headerAuth = httpRequest.getHeader("Authorization");
        String token = null;
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            token = headerAuth.substring(7);
        }
        if (token == null || !jwtUtils.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        List<Notification> notifications = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }
}
