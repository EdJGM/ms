package com.auction.user.service;

import com.auction.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    void deleteUserById(Long id);
    void changePassword(Long userId, String oldPassword, String newPassword);
    boolean existsByUsername(String username);
    List<User> getAllUsers();
    boolean existsByEmail(String email);
    User createUser(User user);
    User getUserById(Long id);
    User updateUser(Long userId, User userUpdate);
    Page<User> getAllUsersPaginated(Pageable pageable, String search);
}
