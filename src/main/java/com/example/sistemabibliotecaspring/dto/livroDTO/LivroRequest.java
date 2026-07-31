package com.example.sistemabibliotecaspring.dto.livroDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LivroRequest {
    @NotNull
    @NotBlank
    private String nome;

    private boolean eh_emprestado;

    public LivroRequest() {}

    public LivroRequest(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public boolean isEh_emprestado() {
        return eh_emprestado;
    }
}
