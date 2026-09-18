package com.example.sistemabibliotecaspring.dto.usuarioDTO;

import com.example.sistemabibliotecaspring.model.Role;

public class UsuarioResponse {
    private String nome;
    private String email;
    private Role role;

    public UsuarioResponse() {}

    public UsuarioResponse(String nome, String email, Role role) {
        this.nome = nome;
        this.email = email;
        this.role = role;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }
}
