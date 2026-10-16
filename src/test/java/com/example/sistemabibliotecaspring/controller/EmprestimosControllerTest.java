package com.example.sistemabibliotecaspring.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

import com.example.sistemabibliotecaspring.configuration.JwtAuthenticatorFilter;
import com.example.sistemabibliotecaspring.dto.emprestimoDTO.request.EmprestimoRequest;
import com.example.sistemabibliotecaspring.dto.emprestimoDTO.response.EmprestimoResponse;
import com.example.sistemabibliotecaspring.exception.RecursoEmConflitoException;
import com.example.sistemabibliotecaspring.exception.RecursoNaoEncontradoException;
import com.example.sistemabibliotecaspring.model.Emprestimo;
import com.example.sistemabibliotecaspring.model.Livro;
import com.example.sistemabibliotecaspring.model.Usuario;
import com.example.sistemabibliotecaspring.repository.EmprestimosRepository;
import com.example.sistemabibliotecaspring.service.EmprestimosService;

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
    private final LocalDate DATA_EMPRESTIMO = LocalDate.of(2026, 11, 5);

    private final long ID_EMPRESTIMO = 1L;
    private final long ID_LIVRO = 1L;
    private final boolean DEVOLVIDO = false;

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
                        .with(user(EMAIL))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound());
    }

    @Test
    public void mostrarEmprestimosUsuarioSucesso() throws Exception {
        int page = 0, size = 10;
        Emprestimo emprestimoExample = new Emprestimo(
                ID_EMPRESTIMO,
                new Usuario(EMAIL, NOME_USUARIO),
                new Livro(ID_LIVRO, NOME_LIVRO, !DEVOLVIDO),
                DATA_EMPRESTIMO,
                DEVOLVIDO
        );
        List<Emprestimo> emprestimoList = new ArrayList<>(List.of(emprestimoExample));
        Pageable pageable = PageRequest.of(page, size);

        when(emprestimosService.mostrarEmprestimosUsuario(any(), anyInt(), anyInt(), anyBoolean()))
                .thenReturn(Objects.requireNonNull(ResponseEntity.status(HttpStatus.OK).body(new PageImpl<>(emprestimoList, pageable, emprestimoList.size())).getBody()).map(EmprestimoResponse::new));

        mockMvc.perform(
                        get("/emprestimos")
                                .param("page", Integer.toString(page))
                                .param("size", Integer.toString(size))
                                .param("devolvido", Boolean.toString(DEVOLVIDO))
                                .with(user(EMAIL))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.PageImpl.content").isArray())
                .andExpect(jsonPath("$.PageImpl.content[0].usuario.nome").value(NOME_USUARIO))
                .andExpect(jsonPath("$.PageImpl.content[0].usuario.email").value(EMAIL))
                .andExpect(jsonPath("$.PageImpl.content[0].livro.id").value(ID_LIVRO))
                .andExpect(jsonPath("$.PageImpl.content[0].livro.nome").value(NOME_LIVRO))
                .andExpect(jsonPath("$.PageImpl.content[0].devolvido").value(DEVOLVIDO))
                .andExpect(jsonPath("$.PageImpl.content[0].data_emprestimo").value(DATA_EMPRESTIMO.toString()))
                .andExpect(jsonPath("$.PageImpl.totalElements").value(emprestimoList.size()))
                .andExpect(jsonPath("$.PageImpl.totalPages").value(1))
                .andExpect(jsonPath("$.PageImpl.size").value(size))
                .andExpect(jsonPath("$.PageImpl.number").value(page));
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
    public void emprestarLivroTestParametroInvalido() throws Exception {
        mockMvc.perform(
                        post("/emprestimos/emprestar")
                                .with(user(NOME_USUARIO))
                                .content("{}")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }


    @Test
    public void emprestarLivroSucesso() throws Exception {
        Emprestimo emprestimoExample = new Emprestimo(
                ID_EMPRESTIMO,
                new Usuario(EMAIL, NOME_USUARIO),
                new Livro(ID_LIVRO, NOME_LIVRO, !DEVOLVIDO),
                DATA_EMPRESTIMO,
                DEVOLVIDO
        );

        when(emprestimosService.emprestarLivro(any(), any(EmprestimoRequest.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(new EmprestimoResponse(emprestimoExample)));

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
                .andExpect(jsonPath("$.Emprestimo.usuario.nome").value(NOME_USUARIO))
                .andExpect(jsonPath("$.Emprestimo.usuario.email").value(EMAIL))
                .andExpect(jsonPath("$.Emprestimo.livro.id").value(ID_LIVRO))
                .andExpect(jsonPath("$.Emprestimo.livro.nome").value(NOME_LIVRO))
                .andExpect(jsonPath("$.Emprestimo.devolvido").value(DEVOLVIDO))
                .andExpect(jsonPath("$.Emprestimo.data_emprestimo").value(DATA_EMPRESTIMO.toString()));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    /// devolverLivro()
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void devolverLivroTestUsuarioNaoEncontrado() throws Exception {
        when(emprestimosService.devolverLivro(any(), any(EmprestimoRequest.class)))
                .thenThrow(new RecursoNaoEncontradoException("usuario não encontrado"));

        mockMvc.perform(
                post("/emprestimos/devolver")
                        .with(user(NOME_USUARIO))
                        .content("""
                                        {
                                            "id_livro": %d
                                        }
                                        """.formatted(ID_LIVRO))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound());
    }

    @Test
    public void devolverLivroTestLivroNaoEncontrado() throws Exception {
        when(emprestimosService.devolverLivro(any(), any(EmprestimoRequest.class)))
                .thenThrow(new RecursoNaoEncontradoException("livro não encontrado"));

        mockMvc.perform(
                        post("/emprestimos/devolver")
                                .with(user(NOME_USUARIO))
                                .content("""
                                        {
                                            "id_livro": %d
                                        }
                                        """.formatted(ID_LIVRO))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void devolverLivroTestEmprestimoNaoEncontrado() throws Exception {
        when(emprestimosService.devolverLivro(any(), any(EmprestimoRequest.class)))
                .thenThrow(new RecursoNaoEncontradoException("emprestimo não encontrado"));

        mockMvc.perform(
                        post("/emprestimos/devolver")
                                .with(user(NOME_USUARIO))
                                .content("""
                                        {
                                            "id_livro": %d
                                        }
                                        """.formatted(ID_LIVRO))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void devolverLivroSucesso() throws Exception {
        LocalDate data_devolucao = LocalDate.of(2026, 11, 19);

        Emprestimo emprestimoExample = new Emprestimo(
                ID_EMPRESTIMO,
                new Usuario(EMAIL, NOME_USUARIO),
                new Livro(ID_LIVRO, NOME_LIVRO, !DEVOLVIDO),
                DATA_EMPRESTIMO,
                true
        );

        emprestimoExample.setData_devolucao(data_devolucao);

        when(emprestimosService.devolverLivro(any(), any(EmprestimoRequest.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body(new EmprestimoResponse(emprestimoExample)));

        mockMvc.perform(
                        post("/emprestimos/devolver")
                                .with(user(NOME_USUARIO))
                                .content("""
                                        {
                                            "id_livro": %d
                                        }
                                        """.formatted(ID_LIVRO))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Emprestimo.usuario.nome").value(NOME_USUARIO))
                .andExpect(jsonPath("$.Emprestimo.usuario.email").value(EMAIL))
                .andExpect(jsonPath("$.Emprestimo.livro.id").value(ID_LIVRO))
                .andExpect(jsonPath("$.Emprestimo.livro.nome").value(NOME_LIVRO))
                .andExpect(jsonPath("$.Emprestimo.devolvido").value(true))
                .andExpect(jsonPath("$.Emprestimo.data_emprestimo").value(DATA_EMPRESTIMO.toString()))
                .andExpect(jsonPath("$.Emprestimo.data_devolucao").value(data_devolucao.toString()));
    }
}
