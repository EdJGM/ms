package com.auction.auth.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.auction.auth.dto.RegisterRequest;
import com.auction.auth.dto.LoginRequest;
import com.auction.auth.dto.UserRegistrationDto;
import com.auction.auth.dto.UserDto;

@FeignClient(name = "user-service")
public interface UserServiceClient {
    @PostMapping("/users/internal/register")
    String register(@RequestBody UserRegistrationDto registrationDto);

    @PostMapping("/users/internal/login")
    String login(@RequestBody LoginRequest loginRequest);

    @GetMapping("/users/exists")
    boolean existsByEmail(@RequestParam String email);

    @GetMapping("/users/by-email")
    UserDto getUserByEmail(@RequestParam String email);
}
