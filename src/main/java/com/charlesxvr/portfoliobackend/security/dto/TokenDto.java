package com.charlesxvr.portfoliobackend.security.dto;

import com.charlesxvr.portfoliobackend.security.models.Token;
import lombok.Data;

import java.util.Date;

@Data
public class TokenDto {
    private Long id;
    private String token;
    private Date createdDate;
    private Long userId;

    public TokenDto(Token token) {
        this.id = token.getId();
        this.token = token.getToken();
        this.createdDate = token.getCreatedDate();
        this.userId = token.getUser().getId();
    }
}
