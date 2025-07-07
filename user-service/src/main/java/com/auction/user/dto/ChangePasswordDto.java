package com.auction.user.dto;

import jakarta.validation.constraints.NotBlank;

public class ChangePasswordDto {
    @NotBlank
    private String oldPassword;
    @NotBlank
    private String newPassword;

    public ChangePasswordDto() {}
    public ChangePasswordDto(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }
    public String getOldPassword() { return oldPassword; }
    public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
