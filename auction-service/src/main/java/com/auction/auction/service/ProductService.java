package com.auction.auction.service;

import com.auction.auction.dto.ProductRequest;
import com.auction.auction.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ProductService {

    // ✅ CRUD básico con lógica multi-tenant
    Product createProduct(ProductRequest productRequest, String ownerUsername);

    List<Product> getProductsByOwner(String ownerUsername);

    Product getProductByIdAndOwner(Long id, String ownerUsername);

    Product updateProduct(Long id, ProductRequest productRequest, String ownerUsername);

    void deleteProduct(Long id, String ownerUsername);

    // ✅ Búsquedas y filtros
    Page<Product> getProductsByOwnerPaginated(String ownerUsername, Pageable pageable);

    Page<Product> getProductsByOwnerAndCategory(String ownerUsername, String categoria, Pageable pageable);

    Page<Product> searchProductsByOwner(String ownerUsername, String searchTerm, Pageable pageable);

    List<Product> getProductsByOwnerAndStatus(String ownerUsername, String estado);

    // ✅ Validaciones de negocio
    boolean canProductBeUsedInAuction(Long productId, String ownerUsername);

    boolean updateProductStatus(Long productId, String newStatus, String ownerUsername);

    // ✅ Estadísticas para el moderador
    long countProductsByOwner(String ownerUsername);

    long countProductsByOwnerAndStatus(String ownerUsername, String estado);
}