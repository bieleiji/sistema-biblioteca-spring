package com.example.sistemabibliotecaspring.exception;

public class RecursoNaoAutorizadoException extends RuntimeException {
    public RecursoNaoAutorizadoException(String message) {
        super(message);
    }
}
