package com.auction.auction.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RoleBasedAuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String requestPath = request.getRequestURI();
        String method = request.getMethod();
        
        // Obtener token
        String token = parseJwt(request);
        if (token != null && jwtUtils.validateJwtToken(token)) {
            String userRole = jwtUtils.getRoleFromJwtToken(token);
            
            // Validar acceso por rol
            if (!hasRequiredRole(requestPath, method, userRole)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Insufficient permissions\", \"requiredRole\":\"" + getRequiredRole(requestPath, method) + "\", \"userRole\":\"" + userRole + "\", \"service\":\"auction-service\"}");
                return;
            }
        }
        
        filterChain.doFilter(request, response);
    }

    private boolean hasRequiredRole(String path, String method, String userRole) {
        // Endpoints públicos
        if (isPublicEndpoint(path)) {
            return true;
        }

        // Si no hay rol en el token, solo permitir endpoints públicos
        if (userRole == null) {
            return false;
        }

        // Endpoints de subastas/productos - Solo Moderador y Administrador
        if (path.contains("/auctions") || path.contains("/products") || path.contains("/subastas")) {
            // GET requests - cualquier usuario autenticado puede ver
            if ("GET".equals(method)) {
                return "PARTICIPANTE".equals(userRole) || "MODERADOR".equals(userRole) || "ADMINISTRADOR".equals(userRole);
            }
            // POST, PUT, DELETE - solo MODERADOR y ADMINISTRADOR
            return "MODERADOR".equals(userRole) || "ADMINISTRADOR".equals(userRole);
        }

        return true; // Otros endpoints no restringidos
    }

    private String getRequiredRole(String path, String method) {
        if (path.contains("/auctions") || path.contains("/products") || path.contains("/subastas")) {
            if ("GET".equals(method)) {
                return "PARTICIPANTE";
            }
            return "MODERADOR";
        }
        return "NONE";
    }

    private boolean isPublicEndpoint(String path) {
        return path.equals("/") || 
               path.startsWith("/health") ||
               path.startsWith("/actuator") ||
               path.startsWith("/swagger") ||
               path.startsWith("/v3/api-docs");
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
