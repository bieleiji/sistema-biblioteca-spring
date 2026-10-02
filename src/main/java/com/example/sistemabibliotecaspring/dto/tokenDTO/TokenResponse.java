package com.example.sistemabibliotecaspring.dto.tokenDTO;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("Token")
public class TokenResponse {
    private String token;

    public TokenResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
