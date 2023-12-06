package com.charlesxvr.portfoliobackend.security.controllers;
import com.charlesxvr.portfoliobackend.exceptions.TokenRefreshException;
import com.charlesxvr.portfoliobackend.security.dto.*;
import com.charlesxvr.portfoliobackend.security.models.JwtResponse;
import com.charlesxvr.portfoliobackend.security.models.TokenRefreshRequest;
import com.charlesxvr.portfoliobackend.security.models.TokenRefreshResponse;
import com.charlesxvr.portfoliobackend.security.models.entities.RefreshToken;
import com.charlesxvr.portfoliobackend.security.models.entities.Token;
import com.charlesxvr.portfoliobackend.security.models.entities.User;
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
    @Autowired
    private UserServiceImp userServiceImp;
    @Autowired
    private JwtServiceImp jwtServiceImp;
    @Autowired
    private AuthenticationServiceImp authenticationServiceImp;
    @Autowired
    private RefreshTokenServiceImp refreshTokenServiceImp;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @PreAuthorize("permitAll")
    @PostMapping("/forgotpassword")
    public ResponseEntity<apiResponseDto> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        String response = userServiceImp.forgotPassword(request.getEmail());
        if(response.startsWith("Invalid")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new apiResponseDto(response));
        }
        apiResponseDto responseDto = userServiceImp.sendResponse(response, request.getEmail());
        return ResponseEntity.ok(responseDto);
    }
    @PreAuthorize("permitAll")
    @PutMapping("/reset-password")
    public ResponseEntity<apiResponseDto> resetPassword(@RequestParam String token,
                                @RequestBody ResetPasswordRequest newPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword.getNewPassword());
        String message = userServiceImp.resetPassword(token, encodedPassword);
        if (message.startsWith("Invalid")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new apiResponseDto(message));
        }
        return ResponseEntity.ok(new apiResponseDto(message));
    }
    @PreAuthorize("permitAll")
    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> login
            (@RequestBody @Valid AuthenticationRequest authRequest) {
        AuthenticationResponse jwtDto = authenticationServiceImp.login(authRequest);
        return ResponseEntity.ok(new JwtResponse(jwtDto.getJwt(),jwtDto.getRefreshToken(),jwtDto.getUser().getId(), jwtDto.getUser().getUsername(), jwtDto.getUser().getEmail(), (List<?>) jwtDto.getUser().getAuthorities()));
    }
    @PreAuthorize("permitAll")
    @PostMapping("/signup")
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        UserDto userResponse = authenticationServiceImp.register(user);
        return ResponseEntity.ok(userResponse);
    }
    @PreAuthorize("permitAll")
    @PostMapping("/check-username")
    public ResponseEntity<apiResponseDto> checkUsername(@RequestBody @Valid usernameDto username) {
        boolean takenUsername = this.userServiceImp.isUsernameTaken(username.getUsername());
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
            InvalidateTokenResult result = this.jwtServiceImp.invalidateToken(token);

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
        return refreshTokenServiceImp.findByToken(requestRefreshToken)
                .map(refreshTokenServiceImp::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    Token token = jwtServiceImp.generateToken(user, authenticationServiceImp.generateExtraClaims(user));
                    jwtServiceImp.invalidateToken(user.getToken().getToken());
                    jwtServiceImp.saveToken(token);
                    refreshTokenServiceImp.deleteByUserId(user.getId());
                    RefreshToken newRefreshToken = refreshTokenServiceImp.createRefreshToken(user.getId());
                    return ResponseEntity.ok(new TokenRefreshResponse(token.getToken(), newRefreshToken.getToken()));
                }).orElseThrow( ()  -> new TokenRefreshException(requestRefreshToken, "Refresh token not in database"));
    }
}
