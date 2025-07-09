package com.auction.auctionquery.repository;

import com.auction.auctionquery.model.AuctionView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuctionViewRepository extends JpaRepository<AuctionView, Long> {

    // ✅ Buscar solo subastas activas
    List<AuctionView> findByActiveTrue();

    // ✅ Buscar subastas activas con paginación
    Page<AuctionView> findByActiveTrue(Pageable pageable);

    // ✅ Buscar subastas activas por categoría
    Page<AuctionView> findByActiveTrueAndCategoria(String categoria, Pageable pageable);

    // ✅ Buscar subastas activas por título o descripción
    @Query("SELECT av FROM AuctionView av WHERE av.active = true AND " +
            "(LOWER(av.titulo) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(av.descripcion) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<AuctionView> findActiveAuctionsBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);

    // ✅ Buscar subastas activas por categoría y término de búsqueda
    @Query("SELECT av FROM AuctionView av WHERE av.active = true AND " +
            "av.categoria = :categoria AND " +
            "(LOWER(av.titulo) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(av.descripcion) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<AuctionView> findActiveAuctionsByCategoryAndSearchTerm(
            @Param("categoria") String categoria,
            @Param("searchTerm") String searchTerm,
            Pageable pageable);

    // ✅ Buscar subasta activa por ID
    Optional<AuctionView> findByIdAndActiveTrue(Long id);

    // ✅ Contar subastas activas
    long countByActiveTrue();

    // ✅ Contar subastas activas por categoría
    long countByActiveTrueAndCategoria(String categoria);
}