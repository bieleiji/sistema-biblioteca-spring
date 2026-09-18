package com.example.sistemabibliotecaspring.dto.usuarioDTO;

public class AtualizarUsuarioResponse {
    private UsuarioResponse usuarioResponse;
    private String token;

    public AtualizarUsuarioResponse() {}

    public AtualizarUsuarioResponse(UsuarioResponse usuarioResponse, String token) {
        this.usuarioResponse = usuarioResponse;
        this.token = token;
    }

    public UsuarioResponse getUsuario() {
        return usuarioResponse;
    }

    public String getToken() {
        return token;
    }
}
