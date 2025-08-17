package com.auction.auctionquery.service;

import com.auction.auctionquery.model.AuctionView;
import com.auction.auctionquery.repository.AuctionViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;

@Service
public class AuctionQueryServiceImpl implements AuctionQueryService {

    @Autowired
    private AuctionViewRepository auctionViewRepository;

    @Override
    public List<AuctionView> getActiveAuctions(String categoria, int page, int limit) {
        try {
            // ✅ Configurar paginación (ordenar por fecha de inicio descendente)
            Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "fechaInicio"));

            Page<AuctionView> auctionPage;

            if (categoria != null && !categoria.trim().isEmpty()) {
                // ✅ Filtrar por categoría
                auctionPage = auctionViewRepository.findByActiveTrueAndCategoria(categoria, pageable);
            } else {
                // ✅ Obtener todas las subastas activas
                auctionPage = auctionViewRepository.findByActiveTrue(pageable);
            }

            return auctionPage.getContent();

        } catch (Exception e) {
            // ✅ En caso de error, retornar lista vacía
            System.err.println("Error al obtener subastas activas: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public AuctionView getAuctionById(Long id) {
        try {
            // ✅ Buscar solo si está activa
            return auctionViewRepository.findByIdAndActiveTrue(id).orElse(null);
        } catch (Exception e) {
            System.err.println("Error al obtener subasta por ID: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<AuctionView> searchAuctions(String searchTerm, String categoria, int page, int limit) {
        try {
            // ✅ Configurar paginación
            Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "fechaInicio"));

            Page<AuctionView> auctionPage;

            if (categoria != null && !categoria.trim().isEmpty() &&
                    searchTerm != null && !searchTerm.trim().isEmpty()) {
                // ✅ Buscar por categoría y término
                auctionPage = auctionViewRepository.findActiveAuctionsByCategoryAndSearchTerm(
                        categoria, searchTerm, pageable);
            } else if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                // ✅ Buscar solo por término
                auctionPage = auctionViewRepository.findActiveAuctionsBySearchTerm(searchTerm, pageable);
            } else if (categoria != null && !categoria.trim().isEmpty()) {
                // ✅ Buscar solo por categoría
                auctionPage = auctionViewRepository.findByActiveTrueAndCategoria(categoria, pageable);
            } else {
                // ✅ Obtener todas las subastas activas
                auctionPage = auctionViewRepository.findByActiveTrue(pageable);
            }

            return auctionPage.getContent();

        } catch (Exception e) {
            System.err.println("Error al buscar subastas: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    //Metodo para obtener estadísticas
    public long getActiveAuctionsCount() {
        try {
            return auctionViewRepository.countByActiveTrue();
        } catch (Exception e) {
            System.err.println("Error al contar subastas activas: " + e.getMessage());
            return 0;
        }
    }

    //Metodo para obtener estadísticas por categoría
    public long getActiveAuctionsCountByCategory(String categoria) {
        try {
            return auctionViewRepository.countByActiveTrueAndCategoria(categoria);
        } catch (Exception e) {
            System.err.println("Error al contar subastas por categoría: " + e.getMessage());
            return 0;
        }
    }

//    private List<AuctionView> createMockAuctions() {
//        List<AuctionView> auctions = new ArrayList<>();
//
//        AuctionView auction1 = new AuctionView();
//        auction1.setId(1L);
//        auction1.setTitulo("iPhone 14 Pro");
//        auction1.setDescripcion("iPhone 14 Pro en excelente estado");
//        auction1.setPrecioActual(new BigDecimal("800.00"));
//        auction1.setPrecioMinimo(new BigDecimal("500.00"));
//        auction1.setCategoria("Electrónicos");
//        auction1.setFechaInicio(LocalDateTime.now().minusDays(1));
//        auction1.setFechaFin(LocalDateTime.now().plusDays(2));
//        auction1.setActive(true);
//        auction1.setVendedor("usuario1");
//        auctions.add(auction1);
//
//        AuctionView auction2 = new AuctionView();
//        auction2.setId(2L);
//        auction2.setTitulo("Laptop Gaming");
//        auction2.setDescripcion("Laptop para gaming de alta gama");
//        auction2.setPrecioActual(new BigDecimal("1200.00"));
//        auction2.setPrecioMinimo(new BigDecimal("900.00"));
//        auction2.setCategoria("Electrónicos");
//        auction2.setFechaInicio(LocalDateTime.now().minusHours(5));
//        auction2.setFechaFin(LocalDateTime.now().plusDays(1));
//        auction2.setActive(true);
//        auction2.setVendedor("usuario2");
//        auctions.add(auction2);
//
//        AuctionView auction3 = new AuctionView();
//        auction3.setId(3L);
//        auction3.setTitulo("Bicicleta de Montaña");
//        auction3.setDescripcion("Bicicleta de montaña profesional");
//        auction3.setPrecioActual(new BigDecimal("350.00"));
//        auction3.setPrecioMinimo(new BigDecimal("200.00"));
//        auction3.setCategoria("Deportes");
//        auction3.setFechaInicio(LocalDateTime.now().minusHours(2));
//        auction3.setFechaFin(LocalDateTime.now().plusHours(18));
//        auction3.setActive(true);
//        auction3.setVendedor("usuario3");
//        auctions.add(auction3);
//
//        return auctions;
//    }
}
