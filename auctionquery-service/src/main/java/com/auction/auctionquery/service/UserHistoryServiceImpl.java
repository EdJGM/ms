package com.auction.auctionquery.service;

import com.auction.auctionquery.model.UserHistory;
import com.auction.auctionquery.model.AuctionResult;
import com.auction.auctionquery.model.BidHistory;
import com.auction.auctionquery.repository.AuctionViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserHistoryServiceImpl implements UserHistoryService {

    @Autowired
    private AuctionViewRepository auctionViewRepository;

    // ✅ AGREGAR: Cliente para comunicarse con bid-service
    // @Autowired
    // private BidServiceClient bidServiceClient;

    @Override
    public UserHistory getUserHistory(String username, int page, int limit) {
        UserHistory history = new UserHistory();
        history.setUsername(username);

        try {
            // ✅ TODO: Implementar consulta real a bid-service
            // Por ahora, mantener datos de ejemplo pero indicar que es temporal
            history.setTotalAuctionsParticipated(5);
            history.setTotalAuctionsWon(2);
            history.setTotalAmountSpent(new BigDecimal("1500.00"));

            // ✅ Crear resultados de subastas de ejemplo (temporal)
            List<AuctionResult> auctionResults = new ArrayList<>();

            AuctionResult result1 = new AuctionResult();
            result1.setAuctionId(1L);
            result1.setAuctionDescription("iPhone 14 Pro");
            result1.setFinalPrice(new BigDecimal("850.00"));
            result1.setEndTime(LocalDateTime.now().minusDays(5));
            result1.setWon(true);
            auctionResults.add(result1);

            AuctionResult result2 = new AuctionResult();
            result2.setAuctionId(2L);
            result2.setAuctionDescription("Laptop Gaming");
            result2.setFinalPrice(new BigDecimal("1300.00"));
            result2.setEndTime(LocalDateTime.now().minusDays(3));
            result2.setWon(false);
            auctionResults.add(result2);

            // ✅ Aplicar paginación simple
            int startIndex = page * limit;
            int endIndex = Math.min(startIndex + limit, auctionResults.size());
            if (startIndex < auctionResults.size()) {
                history.setAuctionResults(auctionResults.subList(startIndex, endIndex));
            } else {
                history.setAuctionResults(new ArrayList<>());
            }

            // ✅ Crear historial de pujas de ejemplo (temporal)
            List<BidHistory> bidHistory = new ArrayList<>();

            BidHistory bid1 = new BidHistory();
            bid1.setAuctionId(1L);
            bid1.setAuctionTitle("iPhone 14 Pro");
            bid1.setBidAmount(new BigDecimal("850.00"));
            bid1.setBidTime(LocalDateTime.now().minusDays(5));
            bid1.setWinningBid(true);
            bidHistory.add(bid1);

            BidHistory bid2 = new BidHistory();
            bid2.setAuctionId(2L);
            bid2.setAuctionTitle("Laptop Gaming");
            bid2.setBidAmount(new BigDecimal("1250.00"));
            bid2.setBidTime(LocalDateTime.now().minusDays(3));
            bid2.setWinningBid(false);
            bidHistory.add(bid2);

            // ✅ Aplicar paginación simple al historial de pujas
            if (startIndex < bidHistory.size()) {
                history.setBidHistory(bidHistory.subList(startIndex, Math.min(endIndex, bidHistory.size())));
            } else {
                history.setBidHistory(new ArrayList<>());
            }

        } catch (Exception e) {
            System.err.println("Error al obtener historial de usuario: " + e.getMessage());
            // ✅ Retornar historial vacío en caso de error
            history.setTotalAuctionsParticipated(0);
            history.setTotalAuctionsWon(0);
            history.setTotalAmountSpent(BigDecimal.ZERO);
            history.setAuctionResults(new ArrayList<>());
            history.setBidHistory(new ArrayList<>());
        }

        return history;
    }

    @Override
    public List<UserHistory> getUserParticipations(String username) {
        List<UserHistory> participations = new ArrayList<>();
        participations.add(getUserHistory(username, 0, 10));
        return participations;
    }
}
