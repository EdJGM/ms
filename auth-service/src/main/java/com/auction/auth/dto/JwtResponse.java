package com.auction.auth.dto;

public class JwtResponse {
    private String accessToken;
    private String refreshToken;
    private String type = "Bearer";
    private String username;
    private String email;
    private int expiresIn;

    public JwtResponse(String accessToken, String username, String email) {
        this.accessToken = accessToken;
        this.username = username;
        this.email = email;
    }

    public JwtResponse(String accessToken, String refreshToken, String username, String email, int expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.username = username;
        this.email = email;
        this.expiresIn = expiresIn;
    }

    // Getters y setters
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getExpiresIn() { return expiresIn; }
    public void setExpiresIn(int expiresIn) { this.expiresIn = expiresIn; }

    // Metodo para compatibilidad con código existente
    public String getToken() { return accessToken; }
    public void setToken(String token) { this.accessToken = token; }
}