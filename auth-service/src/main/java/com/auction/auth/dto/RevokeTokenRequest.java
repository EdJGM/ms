package com.auction.auth.dto;

import jakarta.validation.constraints.NotBlank;

public class RevokeTokenRequest {
    @NotBlank(message = "Username is required")
    private String username;
    
    public RevokeTokenRequest() {}
    
    public RevokeTokenRequest(String username) {
        this.username = username;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
}
