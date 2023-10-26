package com.charlesxvr.portfoliobackend.security.dto;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String newPassword;
}
