package com.example.sistemabiliotecaspring.dto;

import com.example.sistemabiliotecaspring.model.Role;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UsuarioRequest {
    @NotNull
    @NotBlank
    private String nome;

    @NotNull
    @NotBlank
    private String senha;

    @NotNull
    @NotBlank
    @Column(unique = true)
    private String email;

    private Role role;

    public String getNome() {
        return nome;
    }

    public String getSenha() {
        return senha;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
