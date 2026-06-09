package com.example.sistemabiliotecaspring.controller;

import com.example.sistemabiliotecaspring.model.Role;
import com.example.sistemabiliotecaspring.model.Usuario;
import com.example.sistemabiliotecaspring.dto.UsuarioRequest;
import com.example.sistemabiliotecaspring.service.UsuariosService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuariosController {
    private final UsuariosService usuariosService;

    public UsuariosController(UsuariosService usuariosService) {
        this.usuariosService = usuariosService;
    }

    @PostMapping("/criar_conta")
    public ResponseEntity<Object> salvarUsuario(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody UsuarioRequest usuarioRequest) {
        String token = null;
        if(authHeader != null)
            token = authHeader.replace("Bearer ", "");

        return usuariosService.salvarUsuario(usuarioRequest, token);
    }

    @GetMapping
    public Page<Usuario> listarUsuarios(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size){
        return usuariosService.listarUsuarios(page, size);
    }

    @PostMapping("/login")
    public ResponseEntity<String> logar(@RequestBody UsuarioRequest usuarioRequest) {
        return usuariosService.logar(usuarioRequest);
    }
}
