package com.example.sistemabibliotecaspring.controller;

import com.example.sistemabibliotecaspring.configuration.JwtAuthenticatorFilter;
import com.example.sistemabibliotecaspring.dto.emprestimoDTO.EmprestimoRequest;
import com.example.sistemabibliotecaspring.exception.RecursoEmConflitoException;
import com.example.sistemabibliotecaspring.exception.RecursoNaoEncontradoException;
import com.example.sistemabibliotecaspring.model.Emprestimo;
import com.example.sistemabibliotecaspring.model.Livro;
import com.example.sistemabibliotecaspring.model.Usuario;
import com.example.sistemabibliotecaspring.repository.EmprestimosRepository;
import com.example.sistemabibliotecaspring.service.EmprestimosService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmprestimosController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EmprestimosControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmprestimosService emprestimosService;

    @MockitoBean
    private EmprestimosRepository emprestimosRepository;

    @MockitoBean
    private JwtAuthenticatorFilter jwtAuthenticatorFilter;

    private final String EMAIL = "example@gmail.com";
    private final String NOME_USUARIO = "usuarioTeste";
    private final String NOME_LIVRO = "livroTeste";
    private final LocalDate DATA = LocalDate.of(2026, 11, 19);

    private final long ID_LIVRO = 1L;

    ///////////////////////////////////////////////////////////////////////////////////////////
    /// mostrarEmprestimosUsuario()
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void mostrarEmprestimosUsuarioTestUsuarioNaoEncontrado() throws Exception {
        when(emprestimosService.mostrarEmprestimosUsuario(any(), anyInt(), anyInt(), isNull()))
                .thenThrow(new RecursoNaoEncontradoException("usuário não encontrado"));

        mockMvc.perform(
                get("/emprestimos")
                        .param("page", "0")
                        .param("size", "10")
                        .with(user("Gabriel"))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound());
    }

    @Test
    public void mostrarEmprestimosUsuarioSucesso() throws Exception {
        int page = 0, size = 10;
        Emprestimo emprestimoExample = new Emprestimo(
                new Usuario(EMAIL, NOME_USUARIO),
                new Livro(NOME_LIVRO),
                DATA,
                false
        );
        List<Emprestimo> emprestimoList = new ArrayList<>(List.of(emprestimoExample));
        Pageable pageable = PageRequest.of(page, size);

        when(emprestimosService.mostrarEmprestimosUsuario(any(), anyInt(), anyInt(), anyBoolean()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body(new PageImpl<>(emprestimoList, pageable, emprestimoList.size())));

        mockMvc.perform(
                        get("/emprestimos")
                                .param("page", Integer.toString(page))
                                .param("size", Integer.toString(size))
                                .param("devolvido", Boolean.toString(false))
                                .with(user("Gabriel"))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].usuario.nome").value(NOME_USUARIO))
                .andExpect(jsonPath("$.content[0].usuario.email").value(EMAIL))
                .andExpect(jsonPath("$.content[0].livro.nome").value(NOME_LIVRO))
                .andExpect(jsonPath("$.content[0].devolvido").value(false))
                .andExpect(jsonPath("$.content[0].data_emprestimo").value(DATA.toString()))
                .andExpect(jsonPath("$.totalElements").value(emprestimoList.size()))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(size))
                .andExpect(jsonPath("$.number").value(page));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    /// emprestarLivro()
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void emprestarLivroTestUsuarioNaoEncontrado() throws Exception {
        when(emprestimosService.emprestarLivro(any(), any(EmprestimoRequest.class)))
                .thenThrow(new RecursoNaoEncontradoException("usuario não encontrado"));

        mockMvc.perform(
                post("/emprestimos/emprestar")
                        .with(user(NOME_USUARIO))
                        .content("""
                                {
                                    "id_livro": "%d"
                                }
                                """.formatted(ID_LIVRO))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    public void emprestarLivroTestLivroNaoEncontrado() throws Exception {
        when(emprestimosService.emprestarLivro(any(), any(EmprestimoRequest.class)))
                .thenThrow(new RecursoNaoEncontradoException("livro não encontrado"));

        mockMvc.perform(
                post("/emprestimos/emprestar")
                        .with(user(NOME_USUARIO))
                        .content("""
                                {
                                    "id_livro": "%d"
                                }
                                """.formatted(ID_LIVRO))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    public void emprestarLivroTestLivroJaEmprestado() throws Exception {
        when(emprestimosService.emprestarLivro(any(), any(EmprestimoRequest.class)))
                .thenThrow(new RecursoEmConflitoException("livro já foi emprestado"));

        mockMvc.perform(
                post("/emprestimos/emprestar")
                        .with(user(NOME_USUARIO))
                        .content("""
                                {
                                    "id_livro": "%d"
                                }
                                """.formatted(ID_LIVRO))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isConflict());
    }

    @Test
    public void emprestarLivroSucesso() throws Exception {
        Emprestimo emprestimoExample = new Emprestimo(
                new Usuario(EMAIL, NOME_USUARIO),
                new Livro(NOME_LIVRO),
                DATA,
                false
        );
        
        when(emprestimosService.emprestarLivro(any(), any(EmprestimoRequest.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(emprestimoExample));

        mockMvc.perform(
                post("/emprestimos/emprestar")
                        .with(user(NOME_USUARIO))
                        .content("""
                                {
                                    "id_livro": %d
                                }
                                """.formatted(ID_LIVRO))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.usuario.nome").value(NOME_USUARIO))
                .andExpect(jsonPath("$.usuario.email").value(EMAIL))
                .andExpect(jsonPath("$.livro.nome").value(NOME_LIVRO))
                .andExpect(jsonPath("$.devolvido").value(false))
                .andExpect(jsonPath("$.data_emprestimo").value(DATA.toString()));
    }
}
