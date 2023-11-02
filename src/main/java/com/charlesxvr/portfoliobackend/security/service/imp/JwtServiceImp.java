package com.charlesxvr.portfoliobackend.security.service.imp;

import com.charlesxvr.portfoliobackend.security.models.entities.Token;
import com.charlesxvr.portfoliobackend.security.models.entities.User;
import com.charlesxvr.portfoliobackend.security.repository.TokenRepository;
import com.charlesxvr.portfoliobackend.security.service.JwtService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
public class JwtServiceImp implements JwtService {
    @Autowired
    private TokenRepository tokenRepository;
    @Value("${security.jwt.expiration-minutes}")
    private Long EXPIRATION_MINUTES;
    @Value("${security.jwt.secret}")
    private String SECRET_KEY;

    public Token findTokenByUserId(Long userId) {
        Optional<Token> optionalToken  = tokenRepository.findByUserId(userId);
        if (optionalToken.isPresent()) {
            Token token = optionalToken.get();
            boolean isTokenValid = validateToken(token.getToken());
            if (isTokenValid) {
                return token;
            } else return null;
        } else return null;
    }
    @Override
    public Token generateToken(User user, Map<String, Object> extraClaims) {
        Date issuedAt = new Date(System.currentTimeMillis());
        Date expiration = new Date(issuedAt.getTime() + (EXPIRATION_MINUTES * 60 * 1000));
        var jwts = Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(user.getUsername())
                .setIssuedAt(issuedAt).setExpiration(expiration)
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .signWith(generateKey(), SignatureAlgorithm.HS256)
                .compact();

        Token token = new Token();
        token.setToken(jwts);
        token.setCreatedDate(issuedAt);
        token.setUser(user);

        return token;
    }

    private Key generateKey() {

        byte[] secret = Decoders.BASE64.decode(SECRET_KEY);

        return Keys.hmacShaKeyFor(secret);
    }
    public boolean validateToken(String token) {
        try {
            Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

            // Parsea el token y verifica la firma
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Comprueba si el token ha expirado
            Date expirationDate = claims.getExpiration();
            Date now = new Date();
            // El token ha expirado
            return !expirationDate.before(now);

        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            // Manejar excepciones relacionadas con el token JWT
            return false; // El token no es válido
        }
    }
    public String extractUsername(String jwt) {
        return extractAllClaims(jwt).getSubject();
    }

    public Claims extractAllClaims(String jwt) {
        return Jwts.parserBuilder().setSigningKey(generateKey()).build()
                .parseClaimsJws(jwt).getBody();
    }
}
