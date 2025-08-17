package com.auction.auction.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 🔄 NUEVO: Primero intentar usar el rol del header del API Gateway
            String userRole = request.getHeader("X-User-Role");
            String username = request.getHeader("X-User-Id");

            if (userRole != null && username != null) {
                System.out.println("🔍 [AUCTION] Using headers from API Gateway");
                System.out.println("🔍 [AUCTION] Username from header: " + username);
                System.out.println("🔍 [AUCTION] Role from header: " + userRole);

                // Usar el rol del header del API Gateway
                List<SimpleGrantedAuthority> authorities =
                        List.of(new SimpleGrantedAuthority("ROLE_" + userRole));

                User userDetails = new User(username, "", authorities);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("🔍 [AUCTION] Authentication set with authorities: " + authorities);
            } else {
                // Fallback: usar el token JWT directamente
                String jwt = parseJwt(request);
                System.out.println("🔍 [AUCTION] JWT Token: " + (jwt != null ? "Present" : "Null"));
                System.out.println("🔍 [AUCTION] Request URI: " + request.getRequestURI());

                if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                    String usernameFromJwt = jwtUtils.getUserNameFromJwtToken(jwt);
                    String roleFromJwt = jwtUtils.getRoleFromJwtToken(jwt); // Necesitamos este método

                    System.out.println("🔍 [AUCTION] Username from JWT: " + usernameFromJwt);
                    System.out.println("🔍 [AUCTION] Role from JWT: " + roleFromJwt);

                    // Usar el rol del JWT, o PARTICIPANTE por defecto
                    String finalRole = roleFromJwt != null ? roleFromJwt : "PARTICIPANTE";
                    List<SimpleGrantedAuthority> authorities =
                            List.of(new SimpleGrantedAuthority("ROLE_" + finalRole));

                    User userDetails = new User(usernameFromJwt, "", authorities);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("🔍 [AUCTION] Authentication set with authorities: " + authorities);
                } else {
                    System.out.println("🔍 [AUCTION] JWT validation failed or JWT is null");
                }
            }
        } catch (Exception e) {
            System.out.println("🔍 [AUCTION] Exception in AuthTokenFilter: " + e.getMessage());
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
