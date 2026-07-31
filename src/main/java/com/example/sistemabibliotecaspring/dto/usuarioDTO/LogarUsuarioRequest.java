package com.example.sistemabibliotecaspring.dto.usuarioDTO;

import com.example.sistemabibliotecaspring.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LogarUsuarioRequest {
    private String nome;

    @NotNull
    @NotBlank
    private String senha;

    @NotNull
    @NotBlank
    @Email
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
