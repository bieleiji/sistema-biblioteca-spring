package com.example.sistemabiliotecaspring.controller;

import com.example.sistemabiliotecaspring.model.Usuario;
import com.example.sistemabiliotecaspring.dto.UsuarioRequest;
import com.example.sistemabiliotecaspring.service.UsuariosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuariosController {
    private final UsuariosService usuariosService;

    public UsuariosController(UsuariosService usuariosService) {
        this.usuariosService = usuariosService;
    }

    @PostMapping("/criar_conta")
    public Usuario salvarUsuario(@RequestBody UsuarioRequest usuarioRequest) {
        return usuariosService.salvarUsuario(usuarioRequest);
    }

    @GetMapping
    public List<Usuario> listarUsuarios() {
        return usuariosService.listarUsuarios();
    }

    @PostMapping("/login")
    public ResponseEntity<String> logar(@RequestBody UsuarioRequest usuarioRequest) {
        return usuariosService.logar(usuarioRequest);
    }
}
