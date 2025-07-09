package com.auction.auctionquery.service;

import com.auction.auctionquery.model.UserHistory;
import java.util.List;

public interface UserHistoryService {
    UserHistory getUserHistory(String username, int page, int limit);
    List<UserHistory> getUserParticipations(String username);
}
