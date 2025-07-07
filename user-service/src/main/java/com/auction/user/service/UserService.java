package com.auction.user.service;

import com.auction.user.model.User;
import java.util.List;

public interface UserService {
    void deleteUserById(Long id);
    void changePassword(Long userId, String oldPassword, String newPassword);
    boolean existsByUsername(String username);
    List<User> getAllUsers();
    boolean existsByEmail(String email);
    User createUser(User user);
}
