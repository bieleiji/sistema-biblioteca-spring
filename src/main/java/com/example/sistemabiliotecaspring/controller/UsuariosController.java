package com.example.sistemabiliotecaspring.controller;

import com.example.sistemabiliotecaspring.model.Usuario;
import com.example.sistemabiliotecaspring.dto.UsuarioRequest;
import com.example.sistemabiliotecaspring.service.UsuariosService;
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
    public ResponseEntity<Object> salvarUsuario(
            Authentication authentication,
            @Valid @RequestBody UsuarioRequest usuarioRequest) {
        return usuariosService.salvarUsuario(usuarioRequest, authentication);
    }

    @PostMapping("/login")
    public ResponseEntity<String> logar(@Valid @RequestBody UsuarioRequest usuarioRequest) {
        return usuariosService.logar(usuarioRequest);
    }

    @GetMapping("/listar")
    public Page<Usuario> listarUsuarios(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size){
        return usuariosService.listarUsuarios(page, size);
    }

    @PatchMapping("/atualizar")
    public ResponseEntity<String> atualizarUsuario(Authentication authentication,
                                                    @Valid @RequestBody UsuarioRequest usuarioRequest) {

        return usuariosService.atualizarUsuario(authentication, usuarioRequest);
    }

    @DeleteMapping("/excluir")
    public ResponseEntity<String> excluirUsuario(Authentication authentication) {
        return usuariosService.excluirUsuario(authentication);
    }
}
