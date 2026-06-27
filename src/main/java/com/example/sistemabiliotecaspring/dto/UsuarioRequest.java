package com.example.sistemabiliotecaspring.dto;

import com.example.sistemabiliotecaspring.model.Role;

public class UsuarioRequest {
    private String nome;
    private String senha;
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
