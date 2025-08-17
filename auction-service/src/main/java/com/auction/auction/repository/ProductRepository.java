package com.auction.auction.repository;

import com.auction.auction.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // ✅ Multi-tenant: Solo productos del moderador
    List<Product> findByOwnerUsername(String ownerUsername);

    // ✅ Multi-tenant: Producto específico del moderador
    Optional<Product> findByIdAndOwnerUsername(Long id, String ownerUsername);

    // ✅ Filtros adicionales para el moderador
    List<Product> findByOwnerUsernameAndCategoria(String ownerUsername, String categoria);

    List<Product> findByOwnerUsernameAndEstado(String ownerUsername, String estado);

    // ✅ Búsqueda paginada para el moderador
    Page<Product> findByOwnerUsername(String ownerUsername, Pageable pageable);

    Page<Product> findByOwnerUsernameAndCategoria(String ownerUsername, String categoria, Pageable pageable);

    // ✅ Búsqueda por nombre (para el moderador)
    Page<Product> findByOwnerUsernameAndNombreContainingIgnoreCase(String ownerUsername, String nombre, Pageable pageable);
}