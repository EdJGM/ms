package com.auction.auction.repository;

import com.auction.auction.model.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {
    // Métodos personalizados si los necesitas
}
