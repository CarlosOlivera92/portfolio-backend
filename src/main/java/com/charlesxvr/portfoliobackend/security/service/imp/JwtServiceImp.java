package com.charlesxvr.portfoliobackend.security.service.imp;

import com.charlesxvr.portfoliobackend.security.dto.InvalidateTokenResult;
import com.charlesxvr.portfoliobackend.security.models.entities.RefreshToken;
import com.charlesxvr.portfoliobackend.security.models.entities.Token;
import com.charlesxvr.portfoliobackend.security.models.entities.User;
import com.charlesxvr.portfoliobackend.security.repository.RefreshTokenRepository;
import com.charlesxvr.portfoliobackend.security.repository.TokenRepository;
import com.charlesxvr.portfoliobackend.security.repository.UserRepository;
import com.charlesxvr.portfoliobackend.security.service.JwtService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
public class JwtServiceImp implements JwtService {
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Value("${security.jwt.expiration-minutes}")
    private Long EXPIRATION_MINUTES;
    @Value("${security.jwt.secret}")
    private String SECRET_KEY;
    @Override
    public Token findTokenByUserId(Long userId) {
        Optional<Token> optionalToken  = tokenRepository.findByUserId(userId);
        return optionalToken.orElse(null);
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
        token.setExpiryDate(expiration);
        token.setUser(user);
        this.tokenRepository.save(token);
        return token;
    }

    @Override
    public Token saveToken(Token token) {
        return this.tokenRepository.save(token);
    }

    private Key generateKey() {

        byte[] secret = Decoders.BASE64.decode(SECRET_KEY);

        return Keys.hmacShaKeyFor(secret);
    }
    @Override
    public boolean validateToken(String token) {
        try {
            Key key = generateKey();

            //Parse token and verify the sign
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Check if the token is expired
            Date expirationDate = claims.getExpiration();
            Date now = new Date();

            // Token expired
            return !expirationDate.before(now);

        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException  | IllegalArgumentException e) {
            return false;
        }
    }
    @Override
    @Transactional
    public InvalidateTokenResult invalidateToken(String jwtToken) {
        try {
            String token = jwtToken.replace("Bearer ", "");
            if (validateToken(token)) {
                String username = extractAllClaims(token).getSubject();
                Optional<User> userOptional = userRepository.findByUsername(username);

                if (userOptional.isPresent()) {
                    User user = userOptional.get();

                    Token tokenResponseBdd = tokenRepository.findByUser_Id(user.getId());
                    RefreshToken refreshTokenBbdd = refreshTokenRepository.findByUser_Id(user.getId());
                    if (tokenResponseBdd != null) {
                        this.tokenRepository.delete_by_token(tokenResponseBdd.getToken());

                        if (refreshTokenBbdd != null) {
                            refreshTokenRepository.deleteById(refreshTokenBbdd.getId());
                        }
                        return new InvalidateTokenResult(true, "Token deleted successfully.");
                    } else {
                        return new InvalidateTokenResult(false, "Token not found for the user");
                    }
                } else {
                    return new InvalidateTokenResult(false, "User not found");
                }
            } else {
                this.tokenRepository.delete_by_token(token);
                return new InvalidateTokenResult(true, "Expired Token has been deleted");
            }

        } catch (EmptyResultDataAccessException ex) {
            return new InvalidateTokenResult(false, "No results found for token deletion.");
        } catch (Exception ex) {
            return new InvalidateTokenResult(false, "An error occurred during token deletion: " + ex.getMessage());
        }
    }
    @Override
    public Claims extractAllClaims(String jwt) {
        try {
            return Jwts.parserBuilder().setSigningKey(generateKey()).build()
                    .parseClaimsJws(jwt).getBody();
        } catch (Exception e) {
            throw new RuntimeException("An error occurred trying to extract the token claims:  " + e.getMessage());
        }
    }
}
