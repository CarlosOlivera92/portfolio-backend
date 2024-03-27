package com.charlesxvr.portfoliobackend.security.filter;

import com.charlesxvr.portfoliobackend.security.models.entities.User;
import com.charlesxvr.portfoliobackend.security.repository.TokenRepository;
import com.charlesxvr.portfoliobackend.security.service.imp.JwtServiceImp;
import com.charlesxvr.portfoliobackend.security.service.imp.UserServiceImp;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserServiceImp userServiceImp;
    @Autowired
    private JwtServiceImp jwtServiceImp;
    @Autowired
    private TokenRepository tokenRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String usernameFromPath = extractUsernameFromPath(request);

        // Skip filter if no Authorization header or doesn't start with "Bearer"
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract JWT
        String jwt = authHeader.split(" ")[1];

        String requestURI = request.getRequestURI();
        if (requestURI.contains("/logout")) {
            filterChain.doFilter(request, response);
            return;
        }
        // Check if the request is for a restricted endpoint, if so, perform username validation
        if (isRestrictedEndpoint(requestURI)) {
            // Continue with username validation
            boolean isTokenValid = jwtServiceImp.validateToken(jwt);
            try {
                if (isTokenValid) {
                    String usernameFromClaims = jwtServiceImp.extractAllClaims(jwt).getSubject();
                    if (usernameFromPath != null) {
                        // Username validation
                        if (!usernameFromClaims.equals(usernameFromPath)) {
                            throw new RuntimeException("Username mismatch in token and request path");
                        }
                    }


                    // Set authentication object in SecurityContext
                    User user = userServiceImp.findByUsername(usernameFromPath).orElseThrow(() -> new RuntimeException("User not found"));
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            usernameFromPath, null, user.getAuthorities()
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    this.tokenRepository.delete_by_token(jwt);
                    throw new RuntimeException("Invalid JWT token");
                }
            } catch (RuntimeException e) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.getWriter().write(e.getMessage());
                return;
            }
        }
        if (jwtServiceImp.validateToken(jwt)) {
            String usernameFromClaims = jwtServiceImp.extractAllClaims(jwt).getSubject();

            // Set authentication object in SecurityContext
            User user = userServiceImp.findByUsername(usernameFromClaims).orElseThrow(() -> new RuntimeException("User not found"));
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    usernameFromClaims, null, user.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authToken);
            // Continue with filter chain
            filterChain.doFilter(request, response);
        } else {
            this.tokenRepository.delete_by_token(jwt);
        }
    }

    private boolean isRestrictedEndpoint(String requestURI) {
        // Check if the request is for a restricted endpoint
        // You can adjust the logic here based on your specific endpoint patterns
        return requestURI.startsWith("/api/certificates") ||
                requestURI.startsWith("/api/projects") ||
                requestURI.startsWith("/api/educational") ||
                requestURI.startsWith("/api/professional") ||
                requestURI.startsWith("/api/courses");
    }

    private String extractUsernameFromPath(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        Pattern pattern = Pattern.compile("^/api/(certificates|projects|educational|professional|courses)/([^/]+)");
        Matcher matcher = pattern.matcher(requestURI);
        if (matcher.find()) {
            return matcher.group(2);
        }
        return null;
    }
}
