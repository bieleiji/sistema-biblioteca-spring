package com.example.sistemabiliotecaspring.dto;

import jakarta.validation.constraints.NotBlank;

public class UsuarioRequest {
    @NotBlank
    private String nome;

    @NotBlank
    private String senha;

    @NotBlank
    private String email;

    public String getNome() {
        return nome;
    }

    public String getSenha() {
        return senha;
    }

    public String getEmail() {
        return email;
    }
}
