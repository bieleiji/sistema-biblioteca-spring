package com.example.sistemabiliotecaspring.service;

import com.example.sistemabiliotecaspring.model.Role;
import com.example.sistemabiliotecaspring.model.Usuario;
import com.example.sistemabiliotecaspring.repository.UsuariosRepository;
import com.example.sistemabiliotecaspring.dto.UsuarioRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuariosService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuariosRepository usuariosRepository;

    @Autowired
    private TokenService tokenService;

    private boolean ehEmailRepetido(String email) {
        return (usuariosRepository.findByEmail(email) != null);
    }

    public ResponseEntity<Object> salvarUsuario(UsuarioRequest usuarioRequest, String token) {
        String role = "";
        Usuario usuario = new Usuario();

        usuario.setSenha(passwordEncoder.encode(usuarioRequest.getSenha()));
        usuario.setNome(usuarioRequest.getNome());

        if(ehEmailRepetido(usuarioRequest.getEmail()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("este email já está sendo utilizado");
        else usuario.setEmail(usuarioRequest.getEmail());

        if(!token.isBlank())
             role = tokenService.obterClaims(token).get("Role", String.class);

        if (role.equals(Role.ADMIN.toString()) || (!usuarioRequest.getRole().equals(Role.ADMIN) && !usuario.getRole().equals(Role.ADMIN)))
            usuario.setRole(usuarioRequest.getRole());
        else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Apenas ADMINs podem definir outros ADMINs");

        return ResponseEntity.status(HttpStatus.OK).body(usuariosRepository.save(usuario));
    }

    public Page<Usuario> listarUsuarios(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return usuariosRepository.findAll(pageable);
    }

    public ResponseEntity<String> logar(UsuarioRequest usuarioRequest) {
        Usuario usuario = usuariosRepository.findByEmail(usuarioRequest.getEmail());

        if(usuario == null) return  ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        if(!passwordEncoder.matches(usuarioRequest.getSenha(), usuario.getSenha()))
            return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Senha incorreta");

        return ResponseEntity.status(HttpStatus.OK).body(tokenService.gerarToken(usuario));
    }
}
