package com.example.sistemabibliotecaspring.service;

import com.example.sistemabibliotecaspring.dto.usuarioDTO.AtualizarUsuarioRequest;
import com.example.sistemabibliotecaspring.dto.usuarioDTO.AtualizarUsuarioResponse;
import com.example.sistemabibliotecaspring.dto.usuarioDTO.LogarUsuarioRequest;
import com.example.sistemabibliotecaspring.dto.usuarioDTO.SalvarUsuarioRequest;
import com.example.sistemabibliotecaspring.exception.RecursoEmConflitoException;
import com.example.sistemabibliotecaspring.exception.RecursoNaoAutenticadoException;
import com.example.sistemabibliotecaspring.exception.RecursoNaoAutorizadoException;
import com.example.sistemabibliotecaspring.exception.RecursoNaoEncontradoException;
import com.example.sistemabibliotecaspring.model.Role;
import com.example.sistemabibliotecaspring.model.Usuario;
import com.example.sistemabibliotecaspring.repository.UsuariosRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    private final String TOKEN = "token";

    private final Role ROLE_ADMIN = Role.ADMIN;

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

        verify(usuariosRepository).save(new Usuario(NOME, EMAIL, SENHA_CRIPTOGRAFADA,  ROLE_ADMIN));
    }


    // =================================================================================================================
    // logar()
    // =================================================================================================================


    @Test
    public void logarTestUsuarioNaoEncontrado() {
        LogarUsuarioRequest logarUsuarioRequest = mock(LogarUsuarioRequest.class);

        when(logarUsuarioRequest.getEmail()).thenReturn(EMAIL);
        when(usuariosRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> usuariosService.logar(logarUsuarioRequest));
    }

    @Test
    public void logarTestSenhaIncorreta() {
        LogarUsuarioRequest logarUsuarioRequest = mock(LogarUsuarioRequest.class);
        Usuario usuario = new Usuario(NOME, EMAIL,  SENHA_CRIPTOGRAFADA,  ROLE_ADMIN);

        when(logarUsuarioRequest.getEmail()).thenReturn(EMAIL);
        when(logarUsuarioRequest.getSenha()).thenReturn(SENHA);
        when(usuariosRepository.findByEmail(EMAIL)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(SENHA, usuario.getSenha())).thenReturn(false);

        assertThrows(RecursoNaoAutenticadoException.class,
                () -> usuariosService.logar(logarUsuarioRequest));
    }

    @Test
    public void logarSucesso() {
        LogarUsuarioRequest logarUsuarioRequest = mock(LogarUsuarioRequest.class);
        Usuario usuario = new Usuario(NOME, EMAIL,  SENHA_CRIPTOGRAFADA,  ROLE_ADMIN);

        when(logarUsuarioRequest.getEmail()).thenReturn(EMAIL);
        when(logarUsuarioRequest.getSenha()).thenReturn(SENHA);
        when(usuariosRepository.findByEmail(EMAIL)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(SENHA, usuario.getSenha())).thenReturn(true);
        when(tokenService.gerarToken(usuario)).thenReturn(TOKEN);

        String token = usuariosService.logar(logarUsuarioRequest).getBody();

        verify(tokenService).gerarToken(usuario);

        assertEquals(TOKEN, token);
    }

    // =================================================================================================================
    // atualizarUsuario()
    // =================================================================================================================


    @Test
    public void atualizarUsuarioTestUsuarioNaoEncontrado() {
        Authentication authentication = mock(Authentication.class);
        AtualizarUsuarioRequest atualizarUsuarioRequest = mock(AtualizarUsuarioRequest.class);

        when(authentication.getName()).thenReturn(EMAIL);
        when(usuariosRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> usuariosService.atualizarUsuario(authentication, atualizarUsuarioRequest));
    }

    @Test
    public void atualizarUsuarioTestUsuarioNaoEhAdminEQuerDefinirUmAdmin() {
        Authentication authentication = mock(Authentication.class);
        AtualizarUsuarioRequest atualizarUsuarioRequest = mock(AtualizarUsuarioRequest.class);
        Usuario usuario = new Usuario(NOME, EMAIL,  SENHA_CRIPTOGRAFADA,  ROLE_ADMIN);

        when(authentication.getName()).thenReturn(EMAIL);
        when(usuariosRepository.findByEmail(EMAIL)).thenReturn(Optional.of(usuario));
        when(authentication.getAuthorities())
                .thenAnswer(invocation ->
                        List.of(new SimpleGrantedAuthority("ROLE_USUARIO")));
        when(atualizarUsuarioRequest.getRole()).thenReturn(Role.ADMIN);

        assertThrows(RecursoNaoAutorizadoException.class,
                () -> usuariosService.atualizarUsuario(authentication, atualizarUsuarioRequest));
    }

    @Test
    public void atualizarUsuarioSucesso() {
        Authentication authentication = mock(Authentication.class);
        AtualizarUsuarioRequest atualizarUsuarioRequest = mock(AtualizarUsuarioRequest.class);
        Usuario usuario = new Usuario(NOME, EMAIL,  SENHA_CRIPTOGRAFADA,  ROLE_ADMIN);
        String novoNome = "novoNome";
        String  novoEmail = "novoEmail";
        String novoSenha = "novoSenha";
        String novoSenhaCriptografada = "novoSenhaCriptografada";
        Usuario novoUsuario = new Usuario(novoNome, novoEmail, novoSenhaCriptografada, ROLE_ADMIN);

        when(passwordEncoder.encode(novoSenha)).thenReturn(novoSenhaCriptografada);
        when(authentication.getName()).thenReturn(EMAIL);
        when(usuariosRepository.findByEmail(EMAIL)).thenReturn(Optional.of(usuario));
        when(authentication.getAuthorities())
                .thenAnswer(invocation ->
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        when(atualizarUsuarioRequest.getRole()).thenReturn(Role.ADMIN);
        when(atualizarUsuarioRequest.getNome()).thenReturn(novoNome);
        when(atualizarUsuarioRequest.getEmail()).thenReturn(novoEmail);
        when(atualizarUsuarioRequest.getSenha()).thenReturn(novoSenha);
        when(usuariosRepository.save(novoUsuario)).thenReturn(novoUsuario);
        when(tokenService.gerarToken(novoUsuario)).thenReturn(TOKEN);

        AtualizarUsuarioResponse usuarioResponse = usuariosService
                            .atualizarUsuario(authentication, atualizarUsuarioRequest)
                            .getBody();

        assertNotNull(usuarioResponse);

        verify(usuariosRepository).save(novoUsuario);
        verify(tokenService).gerarToken(novoUsuario);
        assertEquals(TOKEN, usuarioResponse.getToken());
    }

    // =================================================================================================================
    // excluirUsuario()
    // =================================================================================================================


    @Test
    public void excluirUsuarioTestUsuarioNaoEncontrado() {
        Authentication authentication = mock(Authentication.class);

        when(authentication.getName()).thenReturn(EMAIL);
        when(usuariosRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> usuariosService.excluirUsuario(authentication));
    }

    @Test
    public void excluirUsuarioSucesso() {
        Authentication authentication = mock(Authentication.class);
        Usuario usuario = new Usuario(EMAIL, NOME);

        when(authentication.getName()).thenReturn(EMAIL);
        when(usuariosRepository.findByEmail(EMAIL)).thenReturn(Optional.of(usuario));

        usuariosService.excluirUsuario(authentication);

        verify(usuariosRepository).delete(usuario);
    }

}
