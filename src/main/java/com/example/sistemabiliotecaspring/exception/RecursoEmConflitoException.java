package com.example.sistemabiliotecaspring.exception;

public class RecursoEmConflitoException extends RuntimeException {
    public RecursoEmConflitoException(String mensagem) {
        super(mensagem);
    }
}
