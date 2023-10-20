package com.charlesxvr.portfoliobackend.security.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse
{
    private String jwt;
}
