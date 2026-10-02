package com.example.sistemabibliotecaspring.dto.livroDTO.response;

import com.example.sistemabibliotecaspring.model.Livro;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("Livro")
public class LivroResponse {
    private long id;
    private String nome;
    private boolean emprestado;

    public LivroResponse() {}

    public LivroResponse(Livro livro) {
        this.id = livro.getId();
        this.nome = livro.getNome();
        this.emprestado = livro.isEmprestado();
    }

    public LivroResponse(long id, String nome, boolean emprestado) {
        this.id = id;
        this.nome = nome;
        this.emprestado = emprestado;
    }

    public long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public boolean isEmprestado() {
        return emprestado;
    }
}
