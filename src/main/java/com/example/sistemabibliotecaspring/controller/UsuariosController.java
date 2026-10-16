package com.example.sistemabibliotecaspring.controller;

import com.example.sistemabibliotecaspring.dto.tokenDTO.TokenResponse;
import com.example.sistemabibliotecaspring.dto.usuarioDTO.request.AtualizarUsuarioRequest;
import com.example.sistemabibliotecaspring.dto.usuarioDTO.request.LogarUsuarioRequest;
import com.example.sistemabibliotecaspring.dto.usuarioDTO.request.SalvarUsuarioRequest;
import com.example.sistemabibliotecaspring.dto.usuarioDTO.response.AtualizarUsuarioResponse;
import com.example.sistemabibliotecaspring.dto.usuarioDTO.response.UsuarioResponse;
import com.example.sistemabibliotecaspring.service.UsuariosService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuariosController {
    private final UsuariosService usuariosService;

    public UsuariosController(UsuariosService usuariosService) {
        this.usuariosService = usuariosService;
    }

    @PostMapping("/criar_conta")
    public ResponseEntity<UsuarioResponse> salvarUsuario(
            Authentication authentication,
            @Valid @RequestBody SalvarUsuarioRequest salvarUsuarioRequest) {
        return usuariosService.salvarUsuario(salvarUsuarioRequest, authentication);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> logar(@Valid @RequestBody LogarUsuarioRequest logarUsuarioRequest) {
        return usuariosService.logar(logarUsuarioRequest);
    }

    @GetMapping("/listar")
    public Page<UsuarioResponse> listarUsuarios(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "10") int size){
        return usuariosService.listarUsuarios(page, size);
    }

    @PatchMapping("/atualizar")
    public ResponseEntity<AtualizarUsuarioResponse> atualizarUsuario(Authentication authentication,
                                                                     @Valid @RequestBody AtualizarUsuarioRequest atualizarUsuarioRequest) {

        return usuariosService.atualizarUsuario(authentication, atualizarUsuarioRequest);
    }

    @DeleteMapping("/excluir")
    public ResponseEntity<String> excluirUsuario(Authentication authentication) {
        return usuariosService.excluirUsuario(authentication);
    }
}
