package com.auction.auction.service;

import com.auction.auction.dto.AuctionRequest;
import com.auction.auction.exception.BusinessRuleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class AuctionValidationService {

    @Autowired
    private ProductService productService;
    
    public void validateAuctionCreation(AuctionRequest request, String ownerUsername) {

        // 1. Verificar que las fechas son válidas
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = request.getFechaInicio();
        LocalDateTime endTime = request.getFechaFin();
        
        if (startTime != null && startTime.isBefore(now)) {
            throw new BusinessRuleException("La fecha de inicio no puede ser en el pasado");
        }
        
        if (startTime != null && endTime != null && endTime.isBefore(startTime)) {
            throw new BusinessRuleException("La fecha de fin debe ser posterior a la de inicio");
        }
        
        // 2. Verificar duración mínima (ej: 1 hora)
        if (startTime != null && endTime != null && Duration.between(startTime, endTime).toHours() < 1) {
            throw new BusinessRuleException("La subasta debe durar al menos 1 hora");
        }
        
        // 3. Verificar precio inicial válido
        BigDecimal precioInicial = request.getPrecioInicial() != null ? request.getPrecioInicial() : request.getStartingPrice();
        if (precioInicial != null && precioInicial.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("El precio inicial debe ser mayor a 0");
        }

        // NUEVA: Validar que el producto puede ser subastado
        if (request.getProductId() != null) {
            boolean canAuction = productService.canProductBeUsedInAuction(request.getProductId(), ownerUsername);
            if (!canAuction) {
                throw new BusinessRuleException("El producto seleccionado no está disponible para subasta");
            }
        }
    }
}
