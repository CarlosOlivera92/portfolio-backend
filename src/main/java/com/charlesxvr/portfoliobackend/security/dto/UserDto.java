package com.charlesxvr.portfoliobackend.security.dto;

import com.charlesxvr.portfoliobackend.security.enums.Role;
import com.charlesxvr.portfoliobackend.security.models.entities.Token;
import com.charlesxvr.portfoliobackend.security.models.entities.User;
import lombok.Data;

import java.util.Date;

@Data
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String profilePicUrl;
    private String aboutMe;
    private String email;
    private Long phoneNumber;
    private String birthday;
    private String username;
    private String password;
    private TokenDto token;
    private Role role;

    public UserDto(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.profilePicUrl = user.getProfilePicUrl();
        this.aboutMe = user.getAboutMe();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber().longValue();
        this.birthday = user.getBirthday().toString();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.token = new TokenDto(user.getToken());
        this.role = user.getRole();
    }

    public static class TokenDTO {
        private Long id;
        private String token;
        private Date createdDate;

        // Constructor, getters, and setters

        public TokenDTO(Token token) {
            this.id = token.getId();
            this.token = token.getToken();
            this.createdDate = token.getCreatedDate();
        }
    }
}
