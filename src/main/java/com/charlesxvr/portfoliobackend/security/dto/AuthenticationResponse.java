package com.charlesxvr.portfoliobackend.security.dto;

import com.charlesxvr.portfoliobackend.security.models.entities.RefreshToken;
import com.charlesxvr.portfoliobackend.security.models.entities.User;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse
{
    private String jwt;
    private String refreshToken;
    private User user;

}
