package com.auction.user.controller;

import com.auction.user.dto.ChangePasswordDto;
import com.auction.user.security.UserDetailsImpl;
import com.auction.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/me")  // Cambiar de "/users/me" a "/me" para coincidir con el path después del StripPrefix
public class UserMeController {

    private final UserService userService;

    @Autowired
    public UserMeController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/password")
    public ResponseEntity<?> changeMyPassword(@RequestBody ChangePasswordDto changePasswordDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        try {
            userService.changePassword(userDetails.getId(), changePasswordDto.getOldPassword(), changePasswordDto.getNewPassword());
            return ResponseEntity.ok().body("Password changed successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}