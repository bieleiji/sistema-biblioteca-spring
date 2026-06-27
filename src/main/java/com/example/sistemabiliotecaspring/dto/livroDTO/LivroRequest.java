package com.example.sistemabiliotecaspring.dto.livroDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LivroRequest {
    @NotNull
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
