package com.example.sistemabibliotecaspring.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;

public class RecursoNaoAutenticadoException extends RuntimeException {
    public RecursoNaoAutenticadoException(String message) {
        super(message);
    }

    public RecursoNaoAutenticadoException(String string, RuntimeException e) {
        super(string, e);
    }
}
