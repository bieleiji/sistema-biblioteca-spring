package com.example.sistemabiliotecaspring.service;

import com.example.sistemabiliotecaspring.model.Usuario;
import com.example.sistemabiliotecaspring.repository.UsuariosRepository;
import com.example.sistemabiliotecaspring.dto.UsuarioRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuariosService {
    private final PasswordEncoder passwordEncoder;
    private final UsuariosRepository usuariosRepository;

    public UsuariosService(UsuariosRepository usuariosRepository, PasswordEncoder passwordEncoder) {
        this.usuariosRepository = usuariosRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario salvarUsuario(UsuarioRequest usuarioRequest) {
        Usuario usuario = new Usuario();
        usuario.setEmail(usuarioRequest.getEmail());
        usuario.setSenha(passwordEncoder.encode(usuarioRequest.getSenha()));
        usuario.setNome(usuarioRequest.getNome());


        return usuariosRepository.save(usuario);
    }

    public Page<Usuario> listarUsuarios(int page, int size) {
        Pageable pageable =  PageRequest.of(page, size);
        return usuariosRepository.findAll(pageable);
    }

    public ResponseEntity<String> logar(UsuarioRequest usuarioRequest) {
        Usuario usuario = usuariosRepository.findByEmail(usuarioRequest.getEmail());

        if(usuario == null) return  ResponseEntity.notFound().build();
        if(!passwordEncoder.matches(usuarioRequest.getSenha(), usuario.getSenha()))
            return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.status(HttpStatus.OK).body(usuario.toString());
    }
}
