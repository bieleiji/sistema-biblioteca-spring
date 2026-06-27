package com.example.sistemabiliotecaspring.dto;

public class LivroRequest {
    private String nome;
    private boolean eh_emprestado;

    public String getNome() {
        return nome;
    }

    public boolean isEh_emprestado() {
        return eh_emprestado;
    }
}
