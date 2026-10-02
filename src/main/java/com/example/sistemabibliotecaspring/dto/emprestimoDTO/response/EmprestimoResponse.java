package com.example.sistemabibliotecaspring.dto.emprestimoDTO.response;

import com.example.sistemabibliotecaspring.dto.livroDTO.response.LivroResponse;
import com.example.sistemabibliotecaspring.dto.usuarioDTO.response.UsuarioResponse;
import com.example.sistemabibliotecaspring.model.Emprestimo;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.time.LocalDate;

@JsonRootName("Emprestimo")
public class EmprestimoResponse {
    private long id;
    private UsuarioResponse usuarioResponse;
    private LivroResponse livroResponse;
    private LocalDate data_emprestimo;
    private LocalDate data_devolucao;
    private boolean devolvido;

    public EmprestimoResponse() {}

    public EmprestimoResponse(Emprestimo emprestimo) {
        this.id = emprestimo.getId();
        this.usuarioResponse = new UsuarioResponse(emprestimo.getUsuario());
        this.livroResponse = new LivroResponse(emprestimo.getLivro());
        this.data_emprestimo = emprestimo.getData_emprestimo();
        this.data_devolucao = emprestimo.getData_devolucao();
        this.devolvido = emprestimo.isDevolvido();
    }

    public EmprestimoResponse(long id, UsuarioResponse usuarioResponse, LivroResponse livroResponse,
                              LocalDate data_emprestimo, LocalDate data_devolucao, boolean devolvido) {
        this.id = id;
        this.usuarioResponse = usuarioResponse;
        this.livroResponse = livroResponse;
        this.data_emprestimo = data_emprestimo;
        this.data_devolucao = data_devolucao;
        this.devolvido = devolvido;
    }

    public long getId() {
        return id;
    }

    public UsuarioResponse getUsuario() {
        return usuarioResponse;
    }

    public LivroResponse getLivro() {
        return livroResponse;
    }

    public LocalDate getData_emprestimo() {
        return data_emprestimo;
    }

    public LocalDate getData_devolucao() {
        return data_devolucao;
    }

    public boolean isDevolvido() {
        return devolvido;
    }
}
