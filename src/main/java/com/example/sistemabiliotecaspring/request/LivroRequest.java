package com.example.sistemabiliotecaspring.request;

public class LivroRequest {
    private String nome;
    private boolean eh_emprestado;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean isEh_emprestado() {
        return eh_emprestado;
    }

    public void setEh_emprestado(boolean eh_emprestado) {
        this.eh_emprestado = eh_emprestado;
    }
}
