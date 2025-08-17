package com.auction.auctionquery.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "auction_view")
public class AuctionView {
    @Id
    private Long id;

    private String titulo;
    private String descripcion;
    private BigDecimal precioMinimo;
    private BigDecimal precioActual;
    private String categoria;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private boolean active;
    private String vendedor;
    private int participantes;

    // Constructors
    public AuctionView() {}

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getPrecioMinimo() { return precioMinimo; }
    public void setPrecioMinimo(BigDecimal precioMinimo) { this.precioMinimo = precioMinimo; }

    public BigDecimal getPrecioActual() { return precioActual; }
    public void setPrecioActual(BigDecimal precioActual) { this.precioActual = precioActual; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDateTime fechaFin) { this.fechaFin = fechaFin; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getVendedor() { return vendedor; }
    public void setVendedor(String vendedor) { this.vendedor = vendedor; }

    public int getParticipantes() { return participantes; }
    public void setParticipantes(int participantes) { this.participantes = participantes; }

    // Metodo para obtener el estado como string basado en el campo active
    public String getEstado() {
        return active ? "activa" : "inactiva";
    }
}