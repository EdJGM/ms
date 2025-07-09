package com.auction.bid.service;

import com.auction.bid.client.AuctionServiceClient;
import com.auction.bid.dto.AuctionDto;
import com.auction.bid.dto.BidRequest;
import com.auction.bid.exception.BusinessRuleException;
import com.auction.bid.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class BidValidationService {

    @Autowired
    private AuctionServiceClient auctionServiceClient;

    public void validateBid(BidRequest bidRequest, Long auctionId, String token) {
        // 1. Verificar que la subasta existe y está activa
        AuctionDto auction = auctionServiceClient.getAuctionById(auctionId, token);
        if (auction == null) {
            throw new ResourceNotFoundException("Subasta no encontrada con ID: " + auctionId);
        }

        // 2. Verificar que la subasta está en estado activo
        if (!"activa".equalsIgnoreCase(auction.getEstado())) {
            throw new BusinessRuleException("La subasta no está activa. Estado actual: " + auction.getEstado());
        }

        // 3. Verificar que la puja es mayor al precio actual + incremento mínimo
        BigDecimal precioActual = auction.getPrecioActual() != null ?
                auction.getPrecioActual() : auction.getStartingPrice();
        BigDecimal incrementoMinimo = auction.getIncrementoMinimo() != null ?
                auction.getIncrementoMinimo() : BigDecimal.ONE;
        BigDecimal minimumBid = precioActual.add(incrementoMinimo);

        if (bidRequest.getBidPrice().compareTo(minimumBid) < 0) {
            throw new BusinessRuleException(
                    String.format("La puja debe ser al menos %s. Precio actual: %s, Incremento mínimo: %s",
                            minimumBid, precioActual, incrementoMinimo)
            );
        }

        // 4. Verificar que la subasta no ha terminado
        if (auction.getFechaFin() != null && auction.getFechaFin().isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException("La subasta ya ha terminado el " + auction.getFechaFin());
        }

        // 5. Verificar que la subasta ya ha comenzado
        if (auction.getFechaInicio() != null && auction.getFechaInicio().isAfter(LocalDateTime.now())) {
            throw new BusinessRuleException("La subasta aún no ha comenzado. Inicia el " + auction.getFechaInicio());
        }
    }

    public AuctionDto getAuctionInfo(Long auctionId, String token) {
        try {
            return auctionServiceClient.getAuctionById(auctionId, token);
        } catch (Exception e) {
            throw new ResourceNotFoundException("No se pudo obtener información de la subasta: " + e.getMessage());
        }
    }

    public boolean isAuctionActive(Long auctionId, String token) {
        try {
            Boolean isActive = auctionServiceClient.isAuctionActive(auctionId, token);
            return isActive != null && isActive;
        } catch (Exception e) {
            return false; // Si hay error en la comunicación, asumir que no está activa
        }
    }
}