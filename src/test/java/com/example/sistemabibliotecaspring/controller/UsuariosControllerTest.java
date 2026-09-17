package com.example.sistemabibliotecaspring.controller;

import com.example.sistemabibliotecaspring.configuration.JwtAuthenticatorFilter;
import com.example.sistemabibliotecaspring.dto.usuarioDTO.LogarUsuarioRequest;
import com.example.sistemabibliotecaspring.dto.usuarioDTO.SalvarUsuarioRequest;
import com.example.sistemabibliotecaspring.exception.RecursoEmConflitoException;
import com.example.sistemabibliotecaspring.exception.RecursoNaoAutenticadoException;
import com.example.sistemabibliotecaspring.exception.RecursoNaoAutorizadoException;
import com.example.sistemabibliotecaspring.exception.RecursoNaoEncontradoException;
import com.example.sistemabibliotecaspring.model.Role;
import com.example.sistemabibliotecaspring.model.Usuario;
import com.example.sistemabibliotecaspring.repository.UsuariosRepository;
import com.example.sistemabibliotecaspring.service.UsuariosService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuariosController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UsuariosControllerTest {
    @Autowired
    public MockMvc mockMvc;

    @MockitoBean
    public UsuariosService usuariosService;

    @MockitoBean
    public UsuariosRepository usuariosRepository;

    @MockitoBean
    public JwtAuthenticatorFilter jwtAuthenticatorFilter;

    private final String NOME = "Gabriel";
    private final String SENHA = "1234";
    private final String SENHA_CRIPTOGRAFADA = "1234Criptografado";
    private final String EMAIL = "teste@gmail.com";
    private final String TOKEN = "tokenTeste";
    private final Role ROLE_USUARIO = Role.USUARIO;


    /////////////////////////////////////////////////////////////////////////////////
    /// salvarUsuario()
    /////////////////////////////////////////////////////////////////////////////////

    @Test
    public void salvarUsuarioTestEmailRepetido() throws Exception {
        when(usuariosService.salvarUsuario(any(SalvarUsuarioRequest.class), any()))
                .thenThrow(new RecursoEmConflitoException("email já está sendo utilizado"));

        mockMvc.perform(
                        post("/usuarios/criar_conta")
                                .content("""
                                {
                                    "nome": "%s",
                                    "senha": "%s",
                                    "email": "%s"
                                }
                                """.formatted(NOME, SENHA, EMAIL))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isConflict());
    }

    @Test
    public void salvarUsuarioTestUsuarioNaoEhAdmin() throws Exception {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USUARIO"));

        when(usuariosService.salvarUsuario(any(SalvarUsuarioRequest.class), any()))
                .thenThrow(new RecursoNaoAutorizadoException("Apenas ADMINs podem definir outros ADMINs"));

        mockMvc.perform(
                        post("/usuarios/criar_conta")
                                .with(
                                        user("Gabriel")
                                                .authorities(authorities)
                                )
                                .content("""
                                {
                                    "nome": "%s",
                                    "senha": "%s",
                                    "email": "%s",
                                    "role": "%s"
                                }
                                """.formatted(NOME, SENHA, EMAIL, Role.ADMIN))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden())
                .andExpect(authenticated().withAuthorities(authorities));
    }

    @Test
    public void salvarUsuarioTestSucesso() throws Exception {
        when(usuariosService.salvarUsuario(any(SalvarUsuarioRequest.class), any()))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(new Usuario(NOME, EMAIL, SENHA_CRIPTOGRAFADA, ROLE_USUARIO)));


        mockMvc.perform(
                        post("/usuarios/criar_conta")
                                .content("""
                                {
                                    "nome": "%s",
                                    "senha": "%s",
                                    "email": "%s"
                                }
                                """.formatted(NOME, SENHA, EMAIL))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value(NOME))
                .andExpect(jsonPath("$.senha").value(SENHA_CRIPTOGRAFADA))
                .andExpect(jsonPath("$.email").value(EMAIL))
                .andExpect(jsonPath("$.role").value(ROLE_USUARIO.name()));
    }

    /////////////////////////////////////////////////////////////////////////////////
    /// logar()
    /////////////////////////////////////////////////////////////////////////////////

    @Test
    public void logarTestUsuairoNaoEncontrado() throws Exception {
        when(usuariosService.logar(any(LogarUsuarioRequest.class)))
                .thenThrow(new RecursoNaoEncontradoException("Usuário não encontrado"));

        mockMvc.perform(
                post("/usuarios/login")
                        .content("""
                                {
                                    "email": "%s",
                                    "senha": "%s"
                                }
                                """.formatted(EMAIL, SENHA))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound());
    }

    @Test
    public void logarTestSenhaIncorreta() throws Exception {
        when(usuariosService.logar(any(LogarUsuarioRequest.class)))
                .thenThrow(new RecursoNaoAutenticadoException("Senha incorreta"));

        mockMvc.perform(
                        post("/usuarios/login")
                                .content("""
                                {
                                    "email": "%s",
                                    "senha": "%s"
                                }
                                """.formatted(EMAIL, SENHA))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void logarSucesso() throws Exception {
        when(usuariosService.logar(any(LogarUsuarioRequest.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body(TOKEN));

        mockMvc.perform(
                        post("/usuarios/login")
                                .content("""
                                {
                                    "email": "%s",
                                    "senha": "%s"
                                }
                                """.formatted(EMAIL, SENHA))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(TOKEN));
    }

    /////////////////////////////////////////////////////////////////////////////////
    /// listarUsuarios()
    /////////////////////////////////////////////////////////////////////////////////

    @Test
    public void listarUsuarios() throws Exception {
        int page = 0, size = 10;

        List<Usuario> lista = new ArrayList<>();
        lista.add(new Usuario(NOME, EMAIL, SENHA_CRIPTOGRAFADA, ROLE_USUARIO));

        Page<Usuario> pagina = new PageImpl<>(lista, PageRequest.of(page, size), lista.size());

        when(usuariosService.listarUsuarios(anyInt() ,anyInt()))
                .thenReturn(pagina);

        mockMvc.perform(
                get("/usuarios/listar")
                        .param("page", Integer.toString(page))
                        .param("size", Integer.toString(size))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].nome").value(NOME))
                .andExpect(jsonPath("$.content[0].email").value(EMAIL))
                .andExpect(jsonPath("$.content[0].senha").value(SENHA_CRIPTOGRAFADA))
                .andExpect(jsonPath("$.content[0].role").value(ROLE_USUARIO.name()))
                .andExpect(jsonPath("$.totalElements").value(lista.size()))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(size))
                .andExpect(jsonPath("$.number").value(page));
    }
}
