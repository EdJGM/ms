package com.auction.user.service;

import com.auction.user.model.User;
import com.auction.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    public User updateUser(Long userId, User userUpdate) {
        User existingUser = userRepository.findById(userId).orElse(null);
        if (existingUser != null) {
            if (userUpdate.getUsername() != null) {
                existingUser.setUsername(userUpdate.getUsername());
            }
            if (userUpdate.getEmail() != null) {
                existingUser.setEmail(userUpdate.getEmail());
            }
            if (userUpdate.getFirstName() != null) {
                existingUser.setFirstName(userUpdate.getFirstName());
            }
            if (userUpdate.getLastName() != null) {
                existingUser.setLastName(userUpdate.getLastName());
            }
            if (userUpdate.getPhoneNumber() != null) {
                existingUser.setPhoneNumber(userUpdate.getPhoneNumber());
            }
            if (userUpdate.getRole() != null) {
                existingUser.setRole(userUpdate.getRole());
            }
            if (userUpdate.getIsActive() != null) {
                existingUser.setIsActive(userUpdate.getIsActive());
            }
            return userRepository.save(existingUser);
        }
        return null;
    }

    @Override
    public Page<User> getAllUsersPaginated(Pageable pageable, String search) {
        if (search != null && !search.trim().isEmpty()) {
            return userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                    search, search, pageable);
        }
        return userRepository.findAll(pageable);
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        if (encoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(encoder.encode(newPassword));
            userRepository.save(user);
        } else {
            throw new RuntimeException("Old password is incorrect");
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User createUser(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
