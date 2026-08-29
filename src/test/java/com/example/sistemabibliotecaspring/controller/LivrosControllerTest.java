package com.example.sistemabibliotecaspring.controller;

import com.example.sistemabibliotecaspring.configuration.JwtAuthenticatorFilter;
import com.example.sistemabibliotecaspring.exception.RecursoNaoEncontradoException;
import com.example.sistemabibliotecaspring.model.Livro;
import com.example.sistemabibliotecaspring.repository.LivrosRepository;
import com.example.sistemabibliotecaspring.service.LivrosService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
}
