package com.example.sistemabiliotecaspring.model;

import jakarta.persistence.*;

@Entity(name = "livros")
public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_livro")
    private Long id;

    private String nome;

    @Column(name = "eh_emprestado")
    private boolean emprestado;

    public Livro() {}

    public Livro(String nome, boolean emprestado) {
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
}
