package com.auction.auth.controller;

import com.auction.auth.dto.*;
import com.auction.auth.security.JwtUtils;
import com.auction.auth.service.TokenService;
import com.auction.auth.service.UserDetailsServiceImpl;
import com.auction.auth.service.UserServiceClient;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

@RestController
@RequestMapping("/")  // Cambiar de "/auth" a "/" para coincidir con el path después del StripPrefix
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserServiceClient userServiceClient;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        System.out.println("=== Login Debug ===");
        System.out.println("Login attempt for email: " + loginRequest.getEmail());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            User userDetails = (User) authentication.getPrincipal();
            System.out.println("Authentication successful for: " + userDetails.getUsername());

            // 🔄 NUEVO: Siempre consultar rol actualizado desde la base de datos
            String currentRole;
            try {
                System.out.println("🔄 [LOGIN] Consulting current role from DB...");
                UserDto userDto = userServiceClient.getUserByEmail(loginRequest.getEmail());
                currentRole = userDto.getRole() != null ? userDto.getRole() : "PARTICIPANTE";
                System.out.println("🔄 [LOGIN] Current role from DB: " + currentRole);
            } catch (Exception e) {
                System.out.println("⚠️ [LOGIN] Error getting role from DB, using default: " + e.getMessage());
                currentRole = "PARTICIPANTE";
            }

            try {
                // Usar TokenService para generar tokens con rol actualizado
                System.out.println("🔄 [LOGIN] Generating tokens with updated role: " + currentRole);
                Map<String, String> tokens = tokenService.generateTokensWithRole(authentication, currentRole);
                System.out.println("✅ [LOGIN] Tokens generated successfully with role: " + currentRole);

                return ResponseEntity.ok(new JwtResponse(
                        tokens.get("accessToken"),
                        tokens.get("refreshToken"),
                        userDetails.getUsername(),
                        userDetails.getUsername(),
                        86400 // 24 horas en segundos
                ));
            } catch (Exception e) {
                System.out.println("⚠️ [LOGIN] Error generating tokens with role, using fallback: " + e.getMessage());
                e.printStackTrace();

                // Si hay error generando tokens con rol, usar método por defecto
                Map<String, String> tokens = tokenService.generateTokens(authentication);

                return ResponseEntity.ok(new JwtResponse(
                        tokens.get("accessToken"),
                        tokens.get("refreshToken"),
                        userDetails.getUsername(),
                        userDetails.getUsername(),
                        86400
                ));
            }
        } catch (Exception authException) {
            System.out.println("❌ [LOGIN] Authentication failed: " + authException.getMessage());
            authException.printStackTrace();
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        // Mapear RegisterRequest a UserRegistrationDto
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setUsername(registerRequest.getUsername());
        registrationDto.setEmail(registerRequest.getEmail());
        registrationDto.setPassword(registerRequest.getPassword());
        registrationDto.setFirstName(registerRequest.getFirstName());
        registrationDto.setLastName(registerRequest.getLastName());
        registrationDto.setPhoneNumber(registerRequest.getPhoneNumber());

        // Llama al user-service para registrar el usuario
        String result = userServiceClient.register(registrationDto);
        return ResponseEntity.ok(result);
    }

    // ✅ MEJORAR: Endpoint de refresh token
    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        try {
            String refreshToken = refreshTokenRequest.getRefreshToken();

            // Verificar que no esté revocado
            if (tokenService.isTokenRevoked(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new MessageResponse("Refresh token has been revoked"));
            }

            // Obtener username del token
            String username = jwtUtils.getUserNameFromJwtToken(refreshToken);

            // 🔄 NUEVO: Obtener rol actualizado desde la base de datos
            String updatedRole;
            try {
                UserDto userDto = userServiceClient.getUserByEmail(username);
                updatedRole = userDto.getRole() != null ? userDto.getRole() : "PARTICIPANTE";
                System.out.println("🔄 [REFRESH] Updated role from DB: " + updatedRole);
            } catch (Exception e) {
                System.out.println("⚠️ [REFRESH] Error getting updated role, using token role");
                updatedRole = jwtUtils.getRoleFromJwtToken(refreshToken);
            }

            // Crear nueva autenticación para generar nuevo refresh token
            User userDetails = (User) userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            // Generar tokens con el rol actualizado
            Map<String, String> tokens = tokenService.generateTokensWithRole(authentication, updatedRole);

            return ResponseEntity.ok(new JwtResponse(
                    tokens.get("accessToken"),
                    tokens.get("refreshToken"),
                    userDetails.getUsername(),
                    userDetails.getUsername(),
                    86400 // 24 horas en segundos
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Token refresh failed: " + e.getMessage()));
        }
    }

    // ✅ MEJORAR: Endpoint de validación de token
    @PostMapping("/validateToken")
    public ResponseEntity<?> validateToken(@RequestBody Map<String, String> tokenRequest) {
        try {
            String token = tokenRequest.get("token");
            if (token == null || token.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Token is required"));
            }

            Map<String, Object> tokenInfo = tokenService.getTokenInfo(token);
            return ResponseEntity.ok(tokenInfo);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error validating token: " + e.getMessage()));
        }
    }

    // ✅ NUEVO: Endpoint de logout
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        try {
            // Extraer token sin el prefijo "Bearer "
            String cleanToken = token.startsWith("Bearer ") ? token.substring(7) : token;

            // Revocar el token
            boolean revoked = tokenService.revokeToken(cleanToken);

            if (revoked) {
                return ResponseEntity.ok(new MessageResponse("Successfully logged out"));
            } else {
                return ResponseEntity.ok(new MessageResponse("No active session found"));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error during logout: " + e.getMessage()));
        }
    }

    // ✅ NUEVO: Endpoint para revocar refresh token
    @PostMapping("/revokeToken")
    public ResponseEntity<?> revokeToken(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String token = request.get("token");

            if (username != null && !username.isEmpty()) {
                // Revocar por username
                boolean revoked = tokenService.revokeRefreshToken(username);
                return ResponseEntity.ok(new MessageResponse(
                        revoked ? "Refresh token revoked successfully" : "No refresh token found for user"));
            } else if (token != null && !token.isEmpty()) {
                // Revocar token específico
                boolean revoked = tokenService.revokeToken(token);
                return ResponseEntity.ok(new MessageResponse(
                        revoked ? "Token revoked successfully" : "Token not found"));
            } else {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Username or token is required"));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error revoking token: " + e.getMessage()));
        }
    }

    // ✅ NUEVO: Endpoint para obtener información de sesiones
    @GetMapping("/sessions")
    public ResponseEntity<?> getActiveSessions(@RequestHeader("Authorization") String token) {
        try {
            String cleanToken = token.startsWith("Bearer ") ? token.substring(7) : token;

            // Validar que el token sea válido
            if (!jwtUtils.validateJwtToken(cleanToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new MessageResponse("Invalid token"));
            }

            Map<String, Object> sessions = tokenService.getActiveSessions();
            return ResponseEntity.ok(sessions);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error getting sessions: " + e.getMessage()));
        }
    }

    // ✅ NUEVO: Endpoint de prueba para roles
    @GetMapping("/roles/test")
    public ResponseEntity<?> testRoles(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            if (token == null || !jwtUtils.validateJwtToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new MessageResponse("Invalid or missing token"));
            }

            String username = jwtUtils.getUserNameFromJwtToken(token);
            String role = jwtUtils.getRoleFromJwtToken(token);

            Map<String, Object> response = Map.of(
                    "message", "Role test successful",
                    "username", username,
                    "role", role != null ? role : "No role found",
                    "endpoint", "/auth/roles/test"
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error testing roles: " + e.getMessage()));
        }
    }

    // ✅ NUEVO: Endpoint de prueba para administrador
    @GetMapping("/admin/test")
    public ResponseEntity<?> testAdminRole(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            if (token == null || !jwtUtils.validateJwtToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new MessageResponse("Invalid or missing token"));
            }

            String role = jwtUtils.getRoleFromJwtToken(token);
            if (!"ADMINISTRADOR".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new MessageResponse("Admin role required"));
            }

            return ResponseEntity.ok(new MessageResponse("Admin access granted"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error testing admin role: " + e.getMessage()));
        }
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
