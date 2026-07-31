package com.example.sistemabibliotecaspring.dto.emprestimoDTO;

import java.time.LocalDate;

public class EmprestimoRequest {
    private Long id_usuario;
    private Long id_livro;
    private LocalDate data_emprestimo;
    private LocalDate date_devolucao;
    private boolean devolvido;

    public EmprestimoRequest(Long id_livro) {
        this.id_livro = id_livro;
    }

    public Long getId_livro() {
        return id_livro;
    }

    public Long getId_usuario() {
        return id_usuario;
    }

    public LocalDate getData_emprestimo() {
        return data_emprestimo;
    }

    public LocalDate getDate_devolucao() {
        return date_devolucao;
    }

    public boolean isDevolvido() {
        return devolvido;
    }
}
