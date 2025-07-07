package com.auction.auth.service;

import com.auction.auth.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserServiceClient userServiceClient;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            UserDto user = userServiceClient.getUserByEmail(email);
            if (user == null) {
                throw new UsernameNotFoundException("User not found with email: " + email);
            }
            
            return User.builder()
                    .username(user.getEmail())
                    .password(user.getPassword())
                    .authorities(new ArrayList<>()) // No roles for now
                    .build();
        } catch (Exception e) {
            throw new UsernameNotFoundException("User not found with email: " + email, e);
        }
    }
}
