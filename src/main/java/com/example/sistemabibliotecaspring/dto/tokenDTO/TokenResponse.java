package com.example.sistemabibliotecaspring.dto.tokenDTO;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("Token")
public record TokenResponse(String token) {
}
