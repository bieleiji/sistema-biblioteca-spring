package com.example.sistemabiliotecaspring.service;

import com.example.sistemabiliotecaspring.dto.usuarioDTO.AtualizarUsuarioRequest;
import com.example.sistemabiliotecaspring.dto.usuarioDTO.LogarUsuarioRequest;
import com.example.sistemabiliotecaspring.exception.RecursoEmConflitoException;
import com.example.sistemabiliotecaspring.exception.RecursoNaoAutenticadoException;
import com.example.sistemabiliotecaspring.exception.RecursoNaoAutorizadoException;
import com.example.sistemabiliotecaspring.exception.RecursoNaoEncontradoException;
import com.example.sistemabiliotecaspring.model.Role;
import com.example.sistemabiliotecaspring.model.Usuario;
import com.example.sistemabiliotecaspring.repository.UsuariosRepository;
import com.example.sistemabiliotecaspring.dto.usuarioDTO.SalvarUsuarioRequest;
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
        return (usuariosRepository.findByEmail(email).isPresent());
    }

    public ResponseEntity<Object> salvarUsuario(SalvarUsuarioRequest salvarUsuarioRequest, Authentication authentication) {
        Usuario usuario = new Usuario();

        if(salvarUsuarioRequest.getRole() == null) salvarUsuarioRequest.setRole(Role.USUARIO);

        usuario.setSenha(passwordEncoder.encode(salvarUsuarioRequest.getSenha()));
        usuario.setNome(salvarUsuarioRequest.getNome());

        if (ehEmailRepetido(salvarUsuarioRequest.getEmail()))
            throw new RecursoEmConflitoException("email já está sendo utilizado");
        else usuario.setEmail(salvarUsuarioRequest.getEmail());

        if(authentication != null) {
            if (authentication.getAuthorities().toString().contains("ROLE_ADMIN") ||
                    !salvarUsuarioRequest.getRole().equals(Role.ADMIN))
                usuario.setRole(salvarUsuarioRequest.getRole());
            else throw new RecursoNaoAutorizadoException("Apenas ADMINs podem definir outros ADMINs");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(usuariosRepository.save(usuario));
    }

    public ResponseEntity<String> logar(LogarUsuarioRequest logarUsuarioRequest) {
        Usuario usuario = usuariosRepository.findByEmail(logarUsuarioRequest.getEmail())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        if(!passwordEncoder.matches(logarUsuarioRequest.getSenha(), usuario.getSenha()))
            throw new RecursoNaoAutenticadoException("Senha incorreta");

        return ResponseEntity.status(HttpStatus.OK).body(tokenService.gerarToken(usuario));
    }

    public Page<Usuario> listarUsuarios(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return usuariosRepository.findAll(pageable);
    }

    private static boolean ehCampoAtualizavel(String dadoNovo) {
        return (dadoNovo != null && !dadoNovo.isBlank());
    }

    public ResponseEntity<String> atualizarUsuario(Authentication authentication,
                                                   AtualizarUsuarioRequest atualizarUsuarioRequest) {
        String role = authentication.getAuthorities().toString();
        Usuario usuario = usuariosRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RecursoNaoEncontradoException("usuario não encontrado"));

        if(authentication.isAuthenticated())
            role = authentication.getAuthorities().toString();

        if(atualizarUsuarioRequest.getRole() != null)
            if (role.contains("ROLE_ADMIN") || (!atualizarUsuarioRequest.getRole().equals(Role.ADMIN)))
                usuario.setRole(atualizarUsuarioRequest.getRole());
            else throw new RecursoNaoAutorizadoException("Apenas ADMINs podem definir outros ADMINs");

        if (ehCampoAtualizavel(atualizarUsuarioRequest.getNome()))
            usuario.setNome(atualizarUsuarioRequest.getNome());

        if (ehCampoAtualizavel(atualizarUsuarioRequest.getSenha()))
            usuario.setSenha(passwordEncoder.encode(atualizarUsuarioRequest.getSenha()));

        if (ehCampoAtualizavel(atualizarUsuarioRequest.getEmail()))
            usuario.setEmail(atualizarUsuarioRequest.getEmail());

        return ResponseEntity.status(HttpStatus.OK).body(usuariosRepository.save(usuario).toString()
                .replace(",", ", \n")
                .replace("Usuario{","\n")
                .replace("}","\n") +
                "\nnovo token:\n" + tokenService.gerarToken(usuario));
    }

    public ResponseEntity<String> excluirUsuario(Authentication authentication) {
        Usuario usuario = usuariosRepository.findByEmail(authentication.getName())
                .orElseThrow(()-> new RecursoNaoEncontradoException("Usuario não encontrado"));

        usuariosRepository.delete(usuario);
        return ResponseEntity.status(HttpStatus.OK).body("Usuario excluído com êxito!");
    }
}
