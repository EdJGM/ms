package com.auction.auction.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-service", url = "http://localhost:8082")
public interface UserServiceClient {
    @GetMapping("/users")
    String getAllUsers(@RequestHeader("Authorization") String token);

    @GetMapping("/users/by-username")
    String getUserByUsername(@RequestParam String username, @RequestHeader("Authorization") String token);
}
