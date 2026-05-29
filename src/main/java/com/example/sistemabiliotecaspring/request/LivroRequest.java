package com.example.sistemabiliotecaspring.request;

import jakarta.validation.constraints.NotBlank;

public class LivroRequest {
    @NotBlank
    private String nome;
    private boolean eh_emprestado;

    public String getNome() {
        return nome;
    }

    public boolean isEh_emprestado() {
        return eh_emprestado;
    }
}
