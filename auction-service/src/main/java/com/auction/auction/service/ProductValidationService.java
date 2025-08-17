package com.auction.auction.service;

import com.auction.auction.dto.ProductRequest;
import com.auction.auction.exception.BusinessRuleException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductValidationService {

    public void validateProductForAuction(Long productId, String ownerUsername) {
        // Esta validación se puede expandir según las reglas de negocio

        // 1. Verificar que el producto existe y pertenece al moderador
        // (esto se maneja en ProductService)

        // 2. Verificar que el producto está disponible
        // (esto se maneja en ProductService.canProductBeUsedInAuction)

        // 3. Reglas adicionales que podrían agregarse:
        // - Verificar que el producto tiene imágenes
        // - Verificar que el precio base es razonable
        // - Verificar que la descripción es adecuada
    }

    public void validateProductData(ProductRequest productRequest) {
        // ✅ Validaciones adicionales de datos

        // Validar longitud de nombre
        if (productRequest.getNombre().length() > 100) {
            throw new BusinessRuleException("El nombre del producto no puede exceder 100 caracteres");
        }

        // Validar longitud de descripción
        if (productRequest.getDescripcion().length() > 1000) {
            throw new BusinessRuleException("La descripción del producto no puede exceder 1000 caracteres");
        }

        // Validar precio base máximo
        if (productRequest.getPrecioBase().compareTo(new BigDecimal("1000000")) > 0) {
            throw new BusinessRuleException("El precio base no puede exceder $1,000,000");
        }

        // Validar número de imágenes
        if (productRequest.getImagenes() != null && productRequest.getImagenes().size() > 10) {
            throw new BusinessRuleException("No se pueden agregar más de 10 imágenes por producto");
        }

        // Validar URLs de imágenes
        if (productRequest.getImagenes() != null) {
            for (String imageUrl : productRequest.getImagenes()) {
                if (!isValidImageUrl(imageUrl)) {
                    throw new BusinessRuleException("URL de imagen inválida: " + imageUrl);
                }
            }
        }
    }

    private boolean isValidImageUrl(String url) {
        // ✅ Validación básica de URL de imagen
        if (url == null || url.trim().isEmpty()) {
            return false;
        }

        // Verificar que es una URL válida y termina en extensión de imagen
        String lowerUrl = url.toLowerCase();
        return lowerUrl.startsWith("http") &&
                (lowerUrl.endsWith(".jpg") || lowerUrl.endsWith(".jpeg") ||
                        lowerUrl.endsWith(".png") || lowerUrl.endsWith(".gif") ||
                        lowerUrl.endsWith(".webp"));
    }
}