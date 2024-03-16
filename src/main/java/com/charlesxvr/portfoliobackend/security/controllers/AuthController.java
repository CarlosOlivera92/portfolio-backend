package com.charlesxvr.portfoliobackend.security.controllers;
import com.charlesxvr.portfoliobackend.exceptions.TokenRefreshException;
import com.charlesxvr.portfoliobackend.security.dto.*;
import com.charlesxvr.portfoliobackend.security.models.JwtResponse;
import com.charlesxvr.portfoliobackend.security.models.TokenRefreshRequest;
import com.charlesxvr.portfoliobackend.security.models.TokenRefreshResponse;
import com.charlesxvr.portfoliobackend.security.models.entities.RefreshToken;
import com.charlesxvr.portfoliobackend.security.models.entities.Token;
import com.charlesxvr.portfoliobackend.security.models.entities.User;
import com.charlesxvr.portfoliobackend.security.service.AuthenticationService;
import com.charlesxvr.portfoliobackend.security.service.JwtService;
import com.charlesxvr.portfoliobackend.security.service.RefreshTokenService;
import com.charlesxvr.portfoliobackend.security.service.UserService;
import com.charlesxvr.portfoliobackend.security.service.imp.AuthenticationServiceImp;
import com.charlesxvr.portfoliobackend.security.service.imp.JwtServiceImp;
import com.charlesxvr.portfoliobackend.security.service.imp.RefreshTokenServiceImp;
import com.charlesxvr.portfoliobackend.security.service.imp.UserServiceImp;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/auth")
@CrossOrigin(origins = "http://localhost:5173/")

public class AuthController {
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public AuthController(AuthenticationService authenticationService, RefreshTokenService refreshTokenService, JwtService jwtService, UserService userService, PasswordEncoder passwordEncoder) {
        this.authenticationService = authenticationService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }
    @PreAuthorize("permitAll")
    @PostMapping("/forgotpassword")
    public ResponseEntity<apiResponseDto> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        String response = userService.forgotPassword(request.getEmail());
        if(response.startsWith("Invalid")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new apiResponseDto(response));
        }
        apiResponseDto responseDto = userService.sendResponse(response, request.getEmail());
        return ResponseEntity.ok(responseDto);
    }
    @PreAuthorize("permitAll")
    @PutMapping("/reset-password")
    public ResponseEntity<apiResponseDto> resetPassword(@RequestParam String token,
                                @RequestBody ResetPasswordRequest newPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword.getNewPassword());
        String message = userService.resetPassword(token, encodedPassword);
        if (message.startsWith("Invalid")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new apiResponseDto(message));
        }
        return ResponseEntity.ok(new apiResponseDto(message));
    }
    @PreAuthorize("permitAll")
    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> login
            (@RequestBody @Valid AuthenticationRequest authRequest) {
        AuthenticationResponse jwtDto = authenticationService.login(authRequest);
        return ResponseEntity.ok(new JwtResponse(jwtDto.getJwt(),jwtDto.getRefreshToken(),jwtDto.getUser().getId(), jwtDto.getUser().getUsername(), jwtDto.getUser().getEmail(), (List<?>) jwtDto.getUser().getAuthorities()));
    }
    @PreAuthorize("permitAll")
    @PostMapping("/signup")
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        UserDto userResponse = authenticationService.register(user);
        return ResponseEntity.ok(userResponse);
    }
    @PreAuthorize("permitAll")
    @PostMapping("/check-username")
    public ResponseEntity<apiResponseDto> checkUsername(@RequestBody @Valid usernameDto username) {
        boolean takenUsername = this.userService.isUsernameTaken(username.getUsername());
        if (takenUsername) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new apiResponseDto("The username is already in use"));
        } else {
            return ResponseEntity.ok(new apiResponseDto("The username is avaliable"));
        }
    }
    @PreAuthorize("permitAll")
    @PostMapping("/logout")
    public ResponseEntity<apiResponseDto> logout(@Valid @RequestHeader("Authorization") String token) {
        try {
            InvalidateTokenResult result = this.jwtService.invalidateToken(token);

            if (result.isStatus()) {
                return ResponseEntity.ok(new apiResponseDto("Session closed successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new apiResponseDto(result.getMessage()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new apiResponseDto("Error closing session"));
        }
    }
    @PreAuthorize("permitAll")
    @PostMapping("/refreshtoken")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request){
        String requestRefreshToken = request.getRefreshToken();
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    Token token = jwtService.generateToken(user, authenticationService.generateExtraClaims(user));
                    jwtService.invalidateToken(user.getToken().getToken());
                    jwtService.saveToken(token);
                    refreshTokenService.deleteByUserId(user.getId());
                    RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getId());
                    return ResponseEntity.ok(new TokenRefreshResponse(token.getToken(), newRefreshToken.getToken()));
                }).orElseThrow( ()  -> new TokenRefreshException(requestRefreshToken, "Refresh token not in database"));
    }
}
