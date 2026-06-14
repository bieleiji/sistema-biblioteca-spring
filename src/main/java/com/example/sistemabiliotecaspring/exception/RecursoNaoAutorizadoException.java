package com.example.sistemabiliotecaspring.exception;

public class RecursoNaoAutorizadoException extends RuntimeException {
    public RecursoNaoAutorizadoException(String mensagem) {
        super(mensagem);
    }
}
