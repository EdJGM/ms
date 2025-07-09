package com.auction.bid.client;

import com.auction.bid.dto.AuctionDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auction-service", url = "http://localhost:8083")
public interface AuctionServiceClient {

    @GetMapping("/subastas/{id}/status")
    Boolean isAuctionActive(@PathVariable Long id, @RequestHeader("Authorization") String token);

    @GetMapping("/subastas/{id}/exists")
    Boolean auctionExists(@PathVariable Long id, @RequestHeader("Authorization") String token);

    @GetMapping("/subastas/{id}")
    AuctionDto getAuctionById(@PathVariable Long id, @RequestHeader("Authorization") String token);
}