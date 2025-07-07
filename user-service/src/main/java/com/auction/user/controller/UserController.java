package com.auction.user.controller;

import com.auction.user.dto.ChangePasswordDto;
import com.auction.user.model.User;
import com.auction.user.service.UserService;
import com.auction.user.security.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, JwtUtils jwtUtils, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public List<User> getAllUsers(HttpServletRequest request) {
        // Extraer JWT del header Authorization
        String headerAuth = request.getHeader("Authorization");
        String token = null;
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            token = headerAuth.substring(7);
        }
        if (token == null || !jwtUtils.validateJwtToken(token)) {
            // Si quieres proteger este endpoint, puedes retornar 401 aquí
            // throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        String username = token != null ? jwtUtils.getUserNameFromJwtToken(token) : null;
        // Ejemplo: Si quieres extraer roles del JWT, puedes hacerlo así (si los roles están en los claims):
        // Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        // List<String> roles = claims.get("roles", List.class);
        return userService.getAllUsers();
    }

    @GetMapping("/by-username")
    public ResponseEntity<User> getUserByUsername(@RequestParam String username) {
        User user = userService.getAllUsers().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/by-email")
    public ResponseEntity<User> getUserByEmail(@RequestParam String email) {
        User user = userService.getAllUsers().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody com.auction.user.dto.UserRegistrationDto registrationDto) {
        if (userService.existsByEmail(registrationDto.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }
        if (userService.existsByUsername(registrationDto.getUsername())) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(registrationDto.getPassword());
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());
        user.setPhoneNumber(registrationDto.getPhoneNumber());
        userService.createUser(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody com.auction.user.dto.UserLoginDto loginDto) {
        User user = userService.getAllUsers().stream()
                .filter(u -> u.getEmail().equals(loginDto.getEmail()))
                .findFirst()
                .orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        // Validación segura de password usando PasswordEncoder
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        
        // Generar JWT token
        com.auction.user.security.UserDetailsImpl userDetails = com.auction.user.security.UserDetailsImpl.build(user);
        org.springframework.security.authentication.UsernamePasswordAuthenticationToken authToken = 
            new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
            );
        String jwtToken = jwtUtils.generateJwtToken(authToken);
        
        // Crear respuesta con token
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("token", jwtToken);
        response.put("type", "Bearer");
        response.put("email", user.getEmail());
        response.put("username", user.getUsername());
        response.put("role", user.getRole().name());
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestParam Long userId, @RequestBody ChangePasswordDto changePasswordDto) {
        userService.changePassword(userId, changePasswordDto.getOldPassword(), changePasswordDto.getNewPassword());
        return ResponseEntity.ok("Password changed successfully");
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        userService.deleteUserById(userId);
    }
}
