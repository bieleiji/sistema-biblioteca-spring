package com.example.sistemabiliotecaspring.service;

import com.example.sistemabiliotecaspring.dto.usuarioDTO.SalvarUsuarioRequest;
import com.example.sistemabiliotecaspring.exception.RecursoEmConflitoException;
import com.example.sistemabiliotecaspring.exception.RecursoNaoAutorizadoException;
import com.example.sistemabiliotecaspring.model.Role;
import com.example.sistemabiliotecaspring.model.Usuario;
import com.example.sistemabiliotecaspring.repository.UsuariosRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UsuariosServiceTest {
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UsuariosRepository usuariosRepository;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private UsuariosService usuariosService;

    private final String SENHA = "senha";
    private final String SENHA_CRIPTOGRAFADA = "senhaCriptografada";
    private final String EMAIL = "email@gmail.com";
    private final String NOME = "nome";

    private final Role ROLE = Role.ADMIN;

    // =================================================================================================================
    // salvarUsuario()
    // =================================================================================================================

    @Test
    public void salvarUsuarioTestEmailJaUtilizado() {
        Authentication authentication = mock(Authentication.class);
        SalvarUsuarioRequest salvarUsuarioRequest = mock(SalvarUsuarioRequest.class);

        when(salvarUsuarioRequest.getEmail()).thenReturn(EMAIL);
        when(usuariosRepository.findByEmail(EMAIL)).thenReturn(Optional.of(new Usuario()));

        assertThrows(RecursoEmConflitoException.class,
                () ->  usuariosService.salvarUsuario(salvarUsuarioRequest, authentication));
    }

    @Test
    public void salvarUsuarioTestUsuarioNaoEhAdminEQuerInserirAdmin() {
        Authentication authentication = mock(Authentication.class);
        SalvarUsuarioRequest salvarUsuarioRequest = mock(SalvarUsuarioRequest.class);

        when(salvarUsuarioRequest.getEmail()).thenReturn(EMAIL);
        when(usuariosRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(authentication.getAuthorities())
                .thenAnswer(invocation ->
                        List.of(new SimpleGrantedAuthority("ROLE_USUARIO")));
        when(salvarUsuarioRequest.getRole()).thenReturn(Role.ADMIN);

        assertThrows(RecursoNaoAutorizadoException.class,
                () -> usuariosService.salvarUsuario(salvarUsuarioRequest, authentication));
    }

    @Test
    public void salvarUsuarioSucesso() {
        Authentication authentication = mock(Authentication.class);
        SalvarUsuarioRequest salvarUsuarioRequest = mock(SalvarUsuarioRequest.class);

        when(passwordEncoder.encode(SENHA)).thenReturn(SENHA_CRIPTOGRAFADA);
        when(salvarUsuarioRequest.getEmail()).thenReturn(EMAIL);
        when(salvarUsuarioRequest.getSenha()).thenReturn(SENHA);
        when(salvarUsuarioRequest.getNome()).thenReturn(NOME);
        when(salvarUsuarioRequest.getRole()).thenReturn(Role.ADMIN);
        when(usuariosRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(authentication.getAuthorities())
                .thenAnswer(invocation ->
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        usuariosService.salvarUsuario(salvarUsuarioRequest, authentication);

        verify(usuariosRepository).save(new Usuario(NOME, EMAIL, SENHA_CRIPTOGRAFADA,  ROLE));
    }
}
