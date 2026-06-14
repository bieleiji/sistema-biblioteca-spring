package com.example.sistemabiliotecaspring.exception;

public class RecursoNaoAutenticadoException extends RuntimeException {
    public RecursoNaoAutenticadoException(String mensagem) {
        super(mensagem);
    }
}
