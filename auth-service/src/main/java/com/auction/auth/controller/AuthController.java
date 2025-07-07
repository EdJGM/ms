package com.auction.auth.controller;

import com.auction.auth.dto.JwtResponse;
import com.auction.auth.dto.LoginRequest;
import com.auction.auth.dto.RegisterRequest;
import com.auction.auth.dto.UserRegistrationDto;
import com.auction.auth.security.JwtUtils;
import com.auction.auth.service.UserServiceClient;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserServiceClient userServiceClient;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User userDetails = (User) authentication.getPrincipal();
        String jwt = jwtUtils.generateJwtToken(authentication);
        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername(), userDetails.getUsername()));
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
}
