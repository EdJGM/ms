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
        
        // Logging para debug
        System.out.println("RoleBasedAuthorizationFilter - Processing path: " + requestPath);
        System.out.println("RoleBasedAuthorizationFilter - Method: " + method);
        System.out.println("RoleBasedAuthorizationFilter - isPublicEndpoint: " + isPublicEndpoint(requestPath));

        // Obtener token
        String token = parseJwt(request);
        if (token != null && jwtUtils.validateJwtToken(token)) {
            String userRole = jwtUtils.getRoleFromJwtToken(token);
            
            System.out.println("RoleBasedAuthorizationFilter - Token found, role: " + userRole);

            // Validar acceso por rol
            if (!hasRequiredRole(requestPath, method, userRole)) {
                System.out.println("RoleBasedAuthorizationFilter - Access denied for role: " + userRole + " on path: " + requestPath);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Insufficient permissions\", \"requiredRole\":\"" + getRequiredRole(requestPath, method) + "\", \"userRole\":\"" + userRole + "\"}");
                return;
            }
        } else {
            System.out.println("RoleBasedAuthorizationFilter - No valid token found");
            // Si es endpoint público, permitir acceso sin token
            if (isPublicEndpoint(requestPath)) {
                System.out.println("RoleBasedAuthorizationFilter - Public endpoint, allowing access without token");
                filterChain.doFilter(request, response);
                return;
            }
        }
        
        System.out.println("RoleBasedAuthorizationFilter - Allowing request to continue");
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
               path.startsWith("/register") ||
               path.startsWith("/login") ||
               path.startsWith("/refreshToken") ||
               path.startsWith("/validateToken") ||
               path.startsWith("/auth/login") ||
               path.startsWith("/auth/register") || 
               path.startsWith("/auth/refreshToken") ||
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
