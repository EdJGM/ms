package com.auction.auth.service;

import com.auction.auth.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenService {

    @Autowired
    private JwtUtils jwtUtils;

    // En producción, esto debería ser una base de datos o Redis
    private final Map<String, String> refreshTokenStore = new ConcurrentHashMap<>();
    private final Map<String, String> revokedTokens = new ConcurrentHashMap<>();

    // ✅ NUEVO: Generar tokens con roles
    public Map<String, String> generateTokensWithRole(Authentication authentication, String role) {
        String accessToken = jwtUtils.generateJwtTokenWithRole(authentication, role);
        String refreshToken = jwtUtils.generateRefreshTokenWithRole(authentication, role);

        // Almacenar el refresh token
        String username = authentication.getName();
        refreshTokenStore.put(username, refreshToken);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return tokens;
    }

    // ✅ NUEVO: Generar tokens sin rol específico
    public Map<String, String> generateTokens(Authentication authentication) {
        return generateTokensWithRole(authentication, "PARTICIPANTE");
    }

    // ✅ NUEVO: Renovar access token usando refresh token
    public String refreshAccessToken(String refreshToken) {
        // Validar el refresh token
        if (!jwtUtils.validateRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        // Verificar que no esté revocado
        if (isTokenRevoked(refreshToken)) {
            throw new RuntimeException("Refresh token has been revoked");
        }

        // Obtener username y rol del refresh token
        String username = jwtUtils.getUserNameFromJwtToken(refreshToken);
        String role = jwtUtils.getRoleFromJwtToken(refreshToken);

        // Verificar que el refresh token almacenado coincida
        String storedRefreshToken = refreshTokenStore.get(username);
        if (!refreshToken.equals(storedRefreshToken)) {
            throw new RuntimeException("Refresh token not found or expired");
        }

        // Crear nueva autenticación para generar nuevo access token
        org.springframework.security.authentication.UsernamePasswordAuthenticationToken authentication =
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        new org.springframework.security.core.userdetails.User(username, "",
                                java.util.Collections.emptyList()),
                        null,
                        java.util.Collections.emptyList()
                );

        // Generar nuevo access token con el mismo rol
        return jwtUtils.generateJwtTokenWithRole(authentication, role != null ? role : "PARTICIPANTE");
    }

    // ✅ NUEVO: Revocar refresh token
    public boolean revokeRefreshToken(String username) {
        String refreshToken = refreshTokenStore.remove(username);
        if (refreshToken != null) {
            revokedTokens.put(refreshToken, username);
            return true;
        }
        return false;
    }

    // ✅ NUEVO: Revocar token específico
    public boolean revokeToken(String token) {
        try {
            String username = jwtUtils.getUserNameFromJwtToken(token);
            revokedTokens.put(token, username);

            // Si es refresh token, también remover del store
            String refreshToken = refreshTokenStore.get(username);
            if (token.equals(refreshToken)) {
                refreshTokenStore.remove(username);
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ✅ NUEVO: Verificar si token está revocado
    public boolean isTokenRevoked(String token) {
        return revokedTokens.containsKey(token);
    }

    // ✅ NUEVO: Validar si refresh token es válido
    public boolean isRefreshTokenValid(String username, String refreshToken) {
        String storedToken = refreshTokenStore.get(username);
        return storedToken != null && storedToken.equals(refreshToken) &&
                jwtUtils.validateRefreshToken(refreshToken) &&
                !isTokenRevoked(refreshToken);
    }

    // ✅ NUEVO: Obtener información del token
    public Map<String, Object> getTokenInfo(String token) {
        Map<String, Object> tokenInfo = new HashMap<>();

        try {
            // Verificar si está revocado
            boolean isRevoked = isTokenRevoked(token);

            boolean isValid = jwtUtils.validateJwtToken(token);
            String tokenType = jwtUtils.getTokenType(token);
            Date expiration = jwtUtils.getExpirationFromToken(token);
            boolean isExpired = jwtUtils.isTokenExpired(token);
            String username = jwtUtils.getUserNameFromJwtToken(token);
            String role = jwtUtils.getRoleFromJwtToken(token);

            tokenInfo.put("valid", isValid && !isRevoked && !isExpired);
            tokenInfo.put("type", tokenType != null ? tokenType : "legacy");
            tokenInfo.put("expired", isExpired);
            tokenInfo.put("revoked", isRevoked);
            tokenInfo.put("username", username);
            tokenInfo.put("role", role);
            tokenInfo.put("expiration", expiration != null ? expiration.toString() : "unknown");

        } catch (Exception e) {
            tokenInfo.put("valid", false);
            tokenInfo.put("error", e.getMessage());
        }

        return tokenInfo;
    }

    // ✅ NUEVO: Limpiar tokens expirados (mantenimiento)
    public void cleanupExpiredTokens() {
        // Remover tokens expirados del store
        refreshTokenStore.entrySet().removeIf(entry -> {
            String token = entry.getValue();
            return jwtUtils.isTokenExpired(token);
        });

        // Remover tokens revocados expirados
        revokedTokens.entrySet().removeIf(entry -> {
            String token = entry.getKey();
            return jwtUtils.isTokenExpired(token);
        });
    }

    // ✅ NUEVO: Obtener sesiones activas
    public Map<String, Object> getActiveSessions() {
        Map<String, Object> sessions = new HashMap<>();
        sessions.put("activeRefreshTokens", refreshTokenStore.size());
        sessions.put("revokedTokens", revokedTokens.size());
        return sessions;
    }
}