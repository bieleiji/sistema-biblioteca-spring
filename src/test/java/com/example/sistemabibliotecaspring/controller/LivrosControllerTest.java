package com.example.sistemabibliotecaspring.controller;

import com.example.sistemabibliotecaspring.configuration.JwtAuthenticatorFilter;
import com.example.sistemabibliotecaspring.dto.livroDTO.request.LivroRequest;
import com.example.sistemabibliotecaspring.dto.livroDTO.response.LivroResponse;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    private final long ID_LIVRO = 1L;

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

        when(livrosService.getLivro(ID_LIVRO))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body(new LivroResponse(new Livro(1L, NOME_LIVRO, false))));

        mockMvc.perform(
                        get("/livros/1")
                                .with(user("gabriel"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Livro.id").value(ID_LIVRO))
                .andExpect(jsonPath("$.Livro.nome").value(NOME_LIVRO))
                .andExpect(jsonPath("$.Livro.emprestado").value(false));
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
                                    "eh_emprestado": false
                                }
                                """.formatted(NOME_LIVRO))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isForbidden());
    }

    @Test
    public void salvarLivroTestSucesso() throws Exception {
        when(livrosService.salvarLivro(any(LivroRequest.class), any()))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(new Livro(ID_LIVRO, NOME_LIVRO, false)));

        mockMvc.perform(
                        post("/livros")
                                .with(
                                        user("gabriel")
                                                .authorities(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                                )
                                .content("""
                                {
                                    "nome": "%s",
                                    "eh_emprestado": false
                                }
                                """.formatted(NOME_LIVRO))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.Livro.id").value(ID_LIVRO))
                .andExpect(jsonPath("$.Livro.nome").value(NOME_LIVRO))
                .andExpect(jsonPath("$.Livro.emprestado").value(false));
    }

    ////////////////////////////////////////////////////////////////////////////////
    /// atualizarLivro()
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    public void atualizarLivroTestUsuarioNaoEhAdmin() throws Exception {
        long id = 1;

        when(livrosService.atualizarLivro(eq(id), any(LivroRequest.class), any()))
                .thenThrow(new RecursoNaoAutorizadoException("Apenas ADMINs podem acrescentar livros ao repositório"));

        mockMvc.perform(
                patch("/livros/{id}",id)
                        .with(
                                user("gabriel")
                                        .authorities(List.of(new SimpleGrantedAuthority("ROLE_USUARIO")))
                        )
                        .content("""
                                {
                                    "nome": "Outro Nome Livro"
                                }
                                """)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isForbidden());
    }

    @Test
    public void atualizarLivroTestLivroNaoEncontrado() throws Exception {
        long id = 99999;

        when(livrosService.atualizarLivro(eq(id), any(LivroRequest.class), any()))
                .thenThrow(new RecursoNaoEncontradoException("livro não encontrado"));

        mockMvc.perform(
                        patch("/livros/{id}",id)
                                .with(
                                        user("gabriel")
                                                .authorities(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                                )
                                .content("""
                                {
                                    "nome": "Outro Nome Livro"
                                }
                                """)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void atualizarLivroSucesso() throws Exception {
        String nomeAlterado = "Outro Nome Livro";
        boolean ehEmprestado = true;

        when(livrosService.atualizarLivro(eq(ID_LIVRO), any(LivroRequest.class), any()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body(new Livro(ID_LIVRO, nomeAlterado, ehEmprestado)));

        mockMvc.perform(
                        patch("/livros/{id}", ID_LIVRO)
                                .with(
                                        user("gabriel")
                                                .authorities(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                                )
                                .content("""
                                {
                                    "nome": "%s",
                                    "eh_emprestado": %b
                                }
                                """.formatted(nomeAlterado, ehEmprestado))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Livro.id").value(ID_LIVRO))
                .andExpect(jsonPath("$.Livro.nome").value(nomeAlterado))
                .andExpect(jsonPath("$.Livro.emprestado").value(ehEmprestado));
    }

    ////////////////////////////////////////////////////////////////////////////////
    /// deletarLivro()
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    public void deletarLivroTestUsuarioNaoEhAdmin() throws Exception {
        long id = 1;

        when(livrosService.deletarLivro(eq(id), any()))
                .thenThrow(new RecursoNaoAutorizadoException("Apenas ADMINs podem deletar livros no repositório"));

        mockMvc.perform(
                delete("/livros/{id}", id)
                        .with(
                                user("gabriel")
                                        .authorities(new SimpleGrantedAuthority("ROLE_USUARIO"))
                        )
        )
                .andExpect(status().isForbidden());
    }

    @Test
    public void deletarLivroTestLivroNaoEncontrado() throws Exception {
        long id = 999999;

        when(livrosService.deletarLivro(eq(id), any()))
                .thenThrow(new RecursoNaoEncontradoException("livro não encontrado"));

        mockMvc.perform(
                delete("/livros/{id}", id)
                        .with(
                                user("gabriel")
                                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))
                        )
        )
                .andExpect(status().isNotFound());
    }

    @Test
    public void deletarLivroSucesso() throws Exception {
        when(livrosService.deletarLivro(eq(ID_LIVRO), any()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body("Livro deletado com sucesso"));

        mockMvc.perform(
                        delete("/livros/{id}", ID_LIVRO)
                                .with(
                                        user("gabriel")
                                                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))
                                )
                )
                .andExpect(status().isOk());
    }
}
