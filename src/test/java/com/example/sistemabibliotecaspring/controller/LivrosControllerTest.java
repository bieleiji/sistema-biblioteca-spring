package com.example.sistemabibliotecaspring.controller;

import com.example.sistemabibliotecaspring.configuration.JwtAuthenticatorFilter;
import com.example.sistemabibliotecaspring.dto.livroDTO.LivroRequest;
import com.example.sistemabibliotecaspring.exception.RecursoNaoAutorizadoException;
import com.example.sistemabibliotecaspring.exception.RecursoNaoEncontradoException;
import com.example.sistemabibliotecaspring.model.Livro;
import com.example.sistemabibliotecaspring.repository.LivrosRepository;
import com.example.sistemabibliotecaspring.service.LivrosService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LivrosController.class)
@AutoConfigureMockMvc(addFilters = false)
public class LivrosControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LivrosService livrosService;

    @MockitoBean
    private LivrosRepository livrosRepository;

    @MockitoBean
    private JwtAuthenticatorFilter jwtAuthenticatorFilter;

    private final String NOME_LIVRO = "Livro de Teste";

    ////////////////////////////////////////////////////////////////////////////////
    /// getLivros()
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    public void getLivros() throws Exception {

        when(livrosService.getLivros(0, 10, null, null))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(
                get("/livros")
                        .with(user("gabriel"))
        ).andExpect(status().isOk());
    }


    ////////////////////////////////////////////////////////////////////////////////
    /// getLivro()
    ////////////////////////////////////////////////////////////////////////////////


    @Test
    public void getLivroTestLivroNaoEncontrado() throws Exception {

        when(livrosService.getLivro(99999))
                .thenThrow(new RecursoNaoEncontradoException("livro não encontrado"));

        mockMvc.perform(
                get("/livros/99999")
                        .with(user("gabriel"))
        ).andExpect(status().isNotFound());
    }

    @Test
    public void getLivroTestLivroSucesso() throws Exception {

        when(livrosService.getLivro(1))
                .thenReturn(new PageImpl<>(List.of(new Livro(1L, "Nome", false))));

        mockMvc.perform(
                        get("/livros/1")
                                .with(user("gabriel"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    ////////////////////////////////////////////////////////////////////////////////
    /// salvarLivro()
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    public void salvarLivroTestUsuarioNaoEhAdmin() throws Exception {
        when(livrosService.salvarLivro(any(LivroRequest.class), any()))
                .thenThrow(new RecursoNaoAutorizadoException("Apenas ADMINs podem acrescentar livros ao repositório"));

        mockMvc.perform(
                post("/livros")
                        .with(
                                user("gabriel")
                                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USUARIO")))
                        )
                        .content("""
                                {
                                    "nome": "%s",
                                    "emprestado": false
                                }
                                """.formatted(NOME_LIVRO))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isForbidden());
    }

    @Test
    public void salvarLivroTestSucesso() throws Exception {
        when(livrosService.salvarLivro(any(LivroRequest.class), any()))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(new Livro(NOME_LIVRO)));

        mockMvc.perform(
                        post("/livros")
                                .with(
                                        user("gabriel")
                                                .authorities(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                                )
                                .content("""
                                {
                                    "nome": "%s",
                                    "emprestado": false
                                }
                                """.formatted(NOME_LIVRO))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value(NOME_LIVRO))
                .andExpect(jsonPath("$.emprestado").value(false));
    }
}
