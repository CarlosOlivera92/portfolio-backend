package com.charlesxvr.portfoliobackend.security.dto;

import com.charlesxvr.portfoliobackend.security.enums.Role;
import com.charlesxvr.portfoliobackend.security.models.entities.Token;
import com.charlesxvr.portfoliobackend.security.models.entities.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
public class UserDto {
    private String firstName;
    private String lastName;
    private String email;
    private Long phoneNumber;
    private String birthday;
    private String username;
    private String profilePic;

    private Role role;


    public UserDto(User user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber().longValue();
        this.birthday = user.getBirthday().toString();
        this.username = user.getUsername();
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
