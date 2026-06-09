package com.example.sistemabiliotecaspring.dto;

import com.example.sistemabiliotecaspring.model.Role;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.ColumnDefault;

public class UsuarioRequest {
    @NotBlank
    private String nome;

    @NotBlank
    private String senha;

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
