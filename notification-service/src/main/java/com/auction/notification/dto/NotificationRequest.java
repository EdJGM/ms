package com.auction.notification.dto;

public class NotificationRequest {
    private Long userId;
    private String title;
    private String message;
    private String type;

    // Constructores
    public NotificationRequest() {}

    public NotificationRequest(Long userId, String title, String message, String type) {
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
    }

    // Getters y setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
