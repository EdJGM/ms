package com.auction.auction.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AuctionRequest {
    private Long productId; // ID del producto a subastar
    private String description;
    private BigDecimal startingPrice;
    private String itemStatus;
    private String itemCategory;
    private int daysToEndTime;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private BigDecimal precioInicial;
    
    // Getters and setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getStartingPrice() { return startingPrice; }
    public void setStartingPrice(BigDecimal startingPrice) { this.startingPrice = startingPrice; }
    public String getItemStatus() { return itemStatus; }
    public void setItemStatus(String itemStatus) { this.itemStatus = itemStatus; }
    public String getItemCategory() { return itemCategory; }
    public void setItemCategory(String itemCategory) { this.itemCategory = itemCategory; }
    public int getDaysToEndTime() { return daysToEndTime; }
    public void setDaysToEndTime(int daysToEndTime) { this.daysToEndTime = daysToEndTime; }
    
    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }
    
    public LocalDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDateTime fechaFin) { this.fechaFin = fechaFin; }
    
    public BigDecimal getPrecioInicial() { return precioInicial; }
    public void setPrecioInicial(BigDecimal precioInicial) { this.precioInicial = precioInicial; }
}
