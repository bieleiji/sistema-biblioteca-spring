package com.example.sistemabiliotecaspring.service;

import com.example.sistemabiliotecaspring.exception.RecursoEmConflitoException;
import com.example.sistemabiliotecaspring.exception.RecursoNaoAutorizadoException;
import com.example.sistemabiliotecaspring.exception.RecursoNaoEncontradoException;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public ResponseEntity<Object> salvarUsuario(UsuarioRequest usuarioRequest, Authentication authentication) {
        String role = "";
        Usuario usuario = new Usuario();

        if(usuarioRequest.getRole() == null) usuarioRequest.setRole(Role.USUARIO);

        usuario.setSenha(passwordEncoder.encode(usuarioRequest.getSenha()));
        usuario.setNome(usuarioRequest.getNome());

        if(ehEmailRepetido(usuarioRequest.getEmail()))
            throw new RecursoEmConflitoException("este email já está sendo utilizado");
        else usuario.setEmail(usuarioRequest.getEmail());

        if(authentication.isAuthenticated())
             role = authentication.getAuthorities().toString();

        if (role.contains("ROLE_ADMIN") || (!usuarioRequest.getRole().equals(Role.ADMIN)))
            usuario.setRole(usuarioRequest.getRole());
        else throw new RecursoNaoAutorizadoException("Apenas ADMINs podem definir outros ADMINs");

        return ResponseEntity.status(HttpStatus.OK).body(usuariosRepository.save(usuario));
    }

    public ResponseEntity<String> logar(UsuarioRequest usuarioRequest) {
        Usuario usuario = usuariosRepository.findByEmail(usuarioRequest.getEmail());

        if(usuario == null) throw new RecursoNaoEncontradoException("Usuário não encontrado");
        if(!passwordEncoder.matches(usuarioRequest.getSenha(), usuario.getSenha()))
            throw new RecursoNaoAutorizadoException("Senha incorreta");

        return ResponseEntity.status(HttpStatus.OK).body(tokenService.gerarToken(usuario));
    }

    public Page<Usuario> listarUsuarios(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return usuariosRepository.findAll(pageable);
    }

    public ResponseEntity<String> atualizarUsuario(Authentication authentication, UsuarioRequest usuarioRequest) {
        String role = authentication.getAuthorities().toString();
        Usuario usuario = usuariosRepository.findByEmail(authentication.getName());

        if(usuario == null)
            throw new RecursoNaoEncontradoException("usuario não encontrado");

        if(usuarioRequest.getNome() != null)
            if(!usuarioRequest.getNome().isBlank())
                usuario.setNome(usuarioRequest.getNome());

        if(usuarioRequest.getSenha() != null)
            if(!usuarioRequest.getSenha().isBlank())
                usuario.setSenha(passwordEncoder.encode(usuarioRequest.getSenha()));

        if(usuarioRequest.getEmail() != null)
            if(!usuarioRequest.getEmail().isBlank())
                if(!ehEmailRepetido(usuarioRequest.getEmail()))
                    usuario.setEmail(usuarioRequest.getEmail());
                else throw new RecursoEmConflitoException("este email já está sendo utilizado");

        if(authentication.isAuthenticated())
            role = authentication.getAuthorities().toString();

        if(usuarioRequest.getRole() != null)
            if (role.contains("ROLE_ADMIN") || (!usuarioRequest.getRole().equals(Role.ADMIN)))
                usuario.setRole(usuarioRequest.getRole());
            else throw new RecursoNaoAutorizadoException("Apenas ADMINs podem definir outros ADMINs");

        return ResponseEntity.status(HttpStatus.OK).body(usuariosRepository.save(usuario).toString()
                .replace(",", ", \n")
                .replace("Usuario{","\n")
                .replace("}","\n") +
                "\nnovo token: " + tokenService.gerarToken(usuario));
    }

    public ResponseEntity<String> excluirUsuario(Authentication authentication) {
        Usuario usuario = usuariosRepository.findByEmail(authentication.getName());

        if(usuario == null)
            throw new RecursoNaoEncontradoException("Usuario não encontrado");

        usuariosRepository.delete(usuario);
        return ResponseEntity.status(HttpStatus.OK).body("Usuario excluído com êxito!");
    }
}
