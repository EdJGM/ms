package com.auction.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private static final String SECRET_KEY = "mi_super_clave_secreta_para_jwt_2025_con_al_menos_64_caracteres_para_HS512_seguridad_completa"; // Same key as auth-service
    private static final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getPath().toString();

            // Logging para debug
            System.out.println("JwtAuthenticationFilter - Processing path: " + path);

            // Lista de endpoints públicos que NO requieren autenticación
            List<String> publicEndpoints = List.of(
                    "/api/v1/auth/register",
                    "/api/v1/auth/login",
                    "/api/v1/auth/refreshToken",
                    "/api/v1/auth/validateToken",
                    "/api/v1/auth/revokeToken",
                    "/register",
                    "/login",
                    "/refreshToken",
                    "/validateToken",
                    "/revokeToken",
                    "/actuator",
                    "/health"
            );

            // Si es un endpoint público, continuar sin validación
            if (isPublicEndpoint(path, publicEndpoints)) {
                System.out.println("JwtAuthenticationFilter - Public endpoint detected: " + path);
                return chain.filter(exchange);
            }

            System.out.println("JwtAuthenticationFilter - Protected endpoint, checking token for: " + path);

            // Extraer token del header Authorization
            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                System.out.println("JwtAuthenticationFilter - No valid Authorization header found");
                return onError(exchange, "Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);
            
            try {
                Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

                System.out.println("JwtAuthenticationFilter - Token validated successfully for user: " + claims.getSubject());

                // Add user information to headers
                ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Id", claims.getSubject())
                    .header("X-User-Email", claims.get("email", String.class))
                    .header("X-User-Role", claims.get("role", String.class))
                    .build();

                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            } catch (Exception e) {
                System.out.println("JwtAuthenticationFilter - Token validation failed: " + e.getMessage());
                return onError(exchange, "Invalid token: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private boolean isPublicEndpoint(String path, List<String> publicEndpoints) {
        boolean isPublic = publicEndpoints.stream().anyMatch(endpoint ->
            path.equals(endpoint) || path.startsWith(endpoint + "/")
        );
        System.out.println("JwtAuthenticationFilter - isPublicEndpoint check: " + path + " -> " + isPublic);
        return isPublic;
    }

    private Mono<Void> onError(org.springframework.web.server.ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        DataBuffer buffer = response.bufferFactory().wrap(err.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    public static class Config {
        // Configuration properties if needed
    }
}
