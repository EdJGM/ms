package com.auction.auctionquery.service;

import com.auction.auctionquery.client.AuctionServiceClient;
import com.auction.auctionquery.model.AuctionView;
import com.auction.auctionquery.repository.AuctionViewRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuctionSyncService {

    @Autowired
    private AuctionServiceClient auctionServiceClient;

    @Autowired
    private AuctionViewRepository auctionViewRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // ✅ Sincronizar cada 5 minutos
    @Scheduled(fixedRate = 300000) // 5 minutos
    public void syncAuctions() {
        try {
            // ✅ Obtener subastas del auction-service
            // Nota: En una implementación real, necesitarías un token de sistema
            String systemToken = "Bearer system-token"; // TODO: Implementar token de sistema

            List<Object> auctionsData = auctionServiceClient.getActiveAuctions(systemToken);

            // ✅ Procesar cada subasta
            for (Object auctionData : auctionsData) {
                try {
                    JsonNode auctionNode = objectMapper.valueToTree(auctionData);

                    // ✅ Crear o actualizar AuctionView
                    AuctionView auctionView = new AuctionView();
                    auctionView.setId(auctionNode.get("auctionId").asLong());
                    auctionView.setTitulo(auctionNode.get("description").asText());
                    auctionView.setDescripcion(auctionNode.get("description").asText());
                    auctionView.setPrecioMinimo(new BigDecimal(auctionNode.get("startingPrice").asText()));
                    auctionView.setPrecioActual(auctionNode.has("precioActual") ?
                            new BigDecimal(auctionNode.get("precioActual").asText()) :
                            new BigDecimal(auctionNode.get("startingPrice").asText()));
                    auctionView.setCategoria(auctionNode.get("itemCategory").asText());
                    auctionView.setVendedor(auctionNode.get("ownerUsername").asText());
                    auctionView.setActive("activa".equals(auctionNode.get("estado").asText()));

                    // ✅ Guardar en la base de datos
                    auctionViewRepository.save(auctionView);

                } catch (Exception e) {
                    System.err.println("Error al procesar subasta: " + e.getMessage());
                }
            }

            System.out.println("✅ Sincronización de subastas completada");

        } catch (Exception e) {
            System.err.println("❌ Error en sincronización de subastas: " + e.getMessage());
        }
    }

    // ✅ Método manual para sincronizar (para testing)
    public void syncAuctionsManually() {
        syncAuctions();
    }
}