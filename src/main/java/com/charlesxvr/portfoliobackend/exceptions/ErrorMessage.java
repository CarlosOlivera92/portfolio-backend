package com.charlesxvr.portfoliobackend.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessage {
    private UUID id = UUID.randomUUID();
    private String message;
    private int statusCode; // Agrega un campo para el código de estado
    private Date timestamp; // Agrega un campo para la marca de tiempo

    public ErrorMessage(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
        this.timestamp = new Date();
    }
}
