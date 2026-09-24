package com.example.sistemabibliotecaspring.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "emprestimos")
public class Emprestimo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_emprestimo")
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_livro", nullable = false)
    private Livro livro;

    private LocalDate data_emprestimo;

    @Column(name = "data_devolucao")
    private LocalDate date_devolucao;
    private boolean devolvido;


    public Emprestimo() {}

    public Emprestimo(Usuario usuario, Livro livro, LocalDate data_emprestimo, boolean devolvido) {
        this.usuario = usuario;
        this.livro = livro;
        this.data_emprestimo = data_emprestimo;
        this.devolvido = devolvido;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Livro getLivro() {
        return livro;
    }

    public void setLivro(Livro livro) {
        this.livro = livro;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDate getData_emprestimo() {
        return data_emprestimo;
    }

    public void setData_emprestimo(LocalDate data_emprestimo) {
        this.data_emprestimo = data_emprestimo;
    }

    public LocalDate getDate_devolucao() {
        return date_devolucao;
    }

    public void setDate_devolucao(LocalDate date_devolucao) {
        this.date_devolucao = date_devolucao;
    }

    public boolean isDevolvido() {
        return devolvido;
    }

    public void setDevolvido(boolean devolvido) {
        this.devolvido = devolvido;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Emprestimo that = (Emprestimo) o;
        return devolvido == that.devolvido && Objects.equals(id, that.id) && Objects.equals(usuario, that.usuario) && Objects.equals(livro, that.livro) && Objects.equals(data_emprestimo, that.data_emprestimo) && Objects.equals(date_devolucao, that.date_devolucao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, usuario, livro, data_emprestimo, date_devolucao, devolvido);
    }
}
