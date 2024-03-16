package com.charlesxvr.portfoliobackend.security.models;

import com.charlesxvr.portfoliobackend.security.enums.Role;
import com.charlesxvr.portfoliobackend.security.models.entities.RefreshToken;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
public class JwtResponse {
    private String token;
    private final String type = "Bearer";
    private String refreshToken;
    private Long id;
    private String username;
    private String email;
    private List<?> roles;
    public JwtResponse(String accessToken, String refreshToken, Long id, String username, String email, List<?> roles) {
        this.token = accessToken;
        this.refreshToken = refreshToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }

}
