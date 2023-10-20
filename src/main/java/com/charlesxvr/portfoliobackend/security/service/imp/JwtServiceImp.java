package com.charlesxvr.portfoliobackend.security.service.imp;

import com.charlesxvr.portfoliobackend.security.models.User;
import com.charlesxvr.portfoliobackend.security.service.JwtService;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Service
public class JwtServiceImp implements JwtService {
    @Value("${security.jwt.expiration-minutes}")
    private Long EXPIRATION_MINUTES;
    @Value("${security.jwt.secret}")
    private String SECRET_KEY;
    @Override
    public String generateToken(User user, Map<String, Object> extraClaims) {
        Date issuedAt = new Date(System.currentTimeMillis());
        Date expiration = new Date(issuedAt.getTime() + (EXPIRATION_MINUTES * 60 * 1000));
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(user.getUsername())
                .setIssuedAt(issuedAt).setExpiration(expiration)
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .signWith(generateKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    private Key generateKey() {

        byte[] secret = Decoders.BASE64.decode(SECRET_KEY);

        return Keys.hmacShaKeyFor(secret);
    }

    public String extractUsername(String jwt) {
        return extractAllClaims(jwt).getSubject();
    }

    private Claims extractAllClaims(String jwt) {
        return Jwts.parserBuilder().setSigningKey(generateKey()).build()
                .parseClaimsJws(jwt).getBody();
    }
}
