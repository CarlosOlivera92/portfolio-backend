package com.charlesxvr.portfoliobackend.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InvalidateTokenResult {
    private boolean status;
    private String message;
}
