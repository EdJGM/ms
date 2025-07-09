package com.auction.auth.security;

import com.auction.auth.security.JwtUtils;
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

        // Verificar si es un endpoint público ANTES de validar el token
        if (isPublicEndpoint(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Obtener token solo para endpoints protegidos
        String token = parseJwt(request);

        if (token != null && jwtUtils.validateJwtToken(token)) {
            String userRole = jwtUtils.getRoleFromJwtToken(token);

            // Validar acceso por rol
            if (!hasRequiredRole(requestPath, method, userRole)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Insufficient permissions\", \"requiredRole\":\"" + getRequiredRole(requestPath, method) + "\", \"userRole\":\"" + userRole + "\"}");
                return;
            }
        } else {
            // No hay token válido para endpoint protegido
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"No valid token provided\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean hasRequiredRole(String path, String method, String userRole) {
        // Endpoints públicos (no requieren roles específicos)
        if (isPublicEndpoint(path)) {
            return true;
        }

        // Si no hay rol en el token, solo permitir endpoints públicos
        if (userRole == null) {
            return false;
        }

        // API 1.2 - Solo Administrador
        if (path.contains("/admin/users")) {
            return "ADMINISTRADOR".equals(userRole);
        }

        // API 2 - Solo Moderador y Administrador
        if (path.contains("/productos") || path.contains("/subastas")) {
            return "MODERADOR".equals(userRole) || "ADMINISTRADOR".equals(userRole);
        }

        // API 3 y 4 - Participante, Moderador o Administrador
        if (path.contains("/pujas") || path.contains("/usuarios/me")) {
            return "PARTICIPANTE".equals(userRole) || "MODERADOR".equals(userRole) || "ADMINISTRADOR".equals(userRole);
        }

        // Endpoints específicos del auth-service
        if (path.startsWith("/auth/")) {
            return true; // Los endpoints de auth manejan su propia autorización
        }

        return true; // Otros endpoints no restringidos
    }

    private String getRequiredRole(String path, String method) {
        if (path.contains("/admin/users")) {
            return "ADMINISTRADOR";
        }
        if (path.contains("/productos") || path.contains("/subastas")) {
            return "MODERADOR";
        }
        if (path.contains("/pujas") || path.contains("/usuarios/me")) {
            return "PARTICIPANTE";
        }
        return "NONE";
    }

    private boolean isPublicEndpoint(String path) {
        return path.equals("/") ||
                path.equals("/register") ||
                path.equals("/login") ||
                path.equals("/refreshToken") ||
                path.equals("/validateToken") ||
                path.startsWith("/auth/login") ||
                path.startsWith("/auth/register") ||
                path.startsWith("/auth/validateToken") ||
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