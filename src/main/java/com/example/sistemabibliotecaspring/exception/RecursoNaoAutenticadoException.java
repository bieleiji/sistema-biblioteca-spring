package com.example.sistemabibliotecaspring.exception;

public class RecursoNaoAutenticadoException extends RuntimeException {
    public RecursoNaoAutenticadoException(String message) {
        super(message);
    }
}
