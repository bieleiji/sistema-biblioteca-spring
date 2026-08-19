package com.example.sistemabibliotecaspring.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity(name = "livros")
public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_livro")
    private Long id;

    private String nome;

    @Column(name = "eh_emprestado")
    private boolean emprestado = false;

    public Livro() {}

    public Livro(String nome) {
        this.nome = nome;
    }

    public Livro(Long id, String nome, boolean emprestado) {
        this.id = id;
        this.nome = nome;
        this.emprestado = emprestado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean isEmprestado() {
        return emprestado;
    }

    public void setEmprestado(boolean emprestado) {
        this.emprestado = emprestado;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Livro livro = (Livro) o;
        return emprestado == livro.emprestado && Objects.equals(id, livro.id) && Objects.equals(nome, livro.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, emprestado);
    }
}
