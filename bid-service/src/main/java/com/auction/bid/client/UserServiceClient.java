package com.auction.bid.client;

import com.auction.bid.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-service") // Remover URL hardcodeada para usar service discovery
public interface UserServiceClient {
    @GetMapping("/users/by-email")
    UserDto getUserByEmail(@RequestParam("email") String email, @RequestHeader("Authorization") String token);
}