package com.auction.auction.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "auctions")
public class Auction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auctionId;

    private String description;
    private BigDecimal startingPrice;
    private String itemStatus;
    private String itemCategory;
    private int daysToEndTime;
    private String ownerUsername;
    private String estado = "programada";
    private BigDecimal precioActual;
    private BigDecimal incrementoMinimo;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    // Getters y setters
    public Long getAuctionId() { return auctionId; }
    public void setAuctionId(Long auctionId) { this.auctionId = auctionId; }
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
    public String getOwnerUsername() { return ownerUsername; }
    public void setOwnerUsername(String ownerUsername) { this.ownerUsername = ownerUsername; }
    
    public BigDecimal getPrecioActual() { return precioActual; }
    public void setPrecioActual(BigDecimal precioActual) { this.precioActual = precioActual; }
    
    public BigDecimal getIncrementoMinimo() { return incrementoMinimo; }
    public void setIncrementoMinimo(BigDecimal incrementoMinimo) { this.incrementoMinimo = incrementoMinimo; }
    
    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }
    
    public LocalDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDateTime fechaFin) { this.fechaFin = fechaFin; }
}
