package com.auction.auctionquery.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "auction-service")
public interface AuctionServiceClient {

    @GetMapping("/subastas")
    List<Object> getActiveAuctions(@RequestHeader("Authorization") String token);

    @GetMapping("/subastas/sync")
    List<Object> getAllAuctionsForSync(@RequestHeader("Authorization") String token);
}