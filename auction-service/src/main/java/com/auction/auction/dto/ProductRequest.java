package com.auction.auction.dto;

import java.math.BigDecimal;
import java.util.List;

public class ProductRequest {
    private String nombre;
    private String descripcion;
    private String categoria;
    private BigDecimal precioBase;
    private List<String> imagenes;

    // Constructores
    public ProductRequest() {}

    public ProductRequest(String nombre, String descripcion, String categoria, BigDecimal precioBase, List<String> imagenes) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.precioBase = precioBase;
        this.imagenes = imagenes;
    }

    // Getters and setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public BigDecimal getPrecioBase() { return precioBase; }
    public void setPrecioBase(BigDecimal precioBase) { this.precioBase = precioBase; }
    public List<String> getImagenes() { return imagenes; }
    public void setImagenes(List<String> imagenes) { this.imagenes = imagenes; }
}