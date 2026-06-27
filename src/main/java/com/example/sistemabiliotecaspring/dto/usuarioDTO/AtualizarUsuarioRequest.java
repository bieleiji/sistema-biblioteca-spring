package com.example.sistemabiliotecaspring.dto.usuarioDTO;

import com.example.sistemabiliotecaspring.model.Role;
import jakarta.validation.constraints.Email;

public class AtualizarUsuarioRequest {
    private String nome;
    private String senha;

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
