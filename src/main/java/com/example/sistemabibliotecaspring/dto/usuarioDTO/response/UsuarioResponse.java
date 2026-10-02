package com.example.sistemabibliotecaspring.dto.usuarioDTO.response;

import com.example.sistemabibliotecaspring.model.Role;
import com.example.sistemabibliotecaspring.model.Usuario;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.Objects;

@JsonRootName("Usuario")
public class UsuarioResponse {
    private String nome;
    private String email;
    private Role role;

    public UsuarioResponse() {}

    public UsuarioResponse(Usuario usuario) {
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.role = usuario.getRole();
    }

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UsuarioResponse that = (UsuarioResponse) o;
        return Objects.equals(nome, that.nome) && Objects.equals(email, that.email) && role == that.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, email, role);
    }
}
