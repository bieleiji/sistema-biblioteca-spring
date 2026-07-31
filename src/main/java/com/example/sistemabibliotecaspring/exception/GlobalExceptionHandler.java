package com.example.sistemabibliotecaspring.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleException(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<String> handleException(RecursoNaoEncontradoException e)  {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(RecursoNaoAutorizadoException.class)
    public ResponseEntity<String> handleException(RecursoNaoAutorizadoException e)  {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }

    @ExceptionHandler(RecursoNaoAutenticadoException.class)
    public ResponseEntity<String> handleException(RecursoNaoAutenticadoException e)  {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    @ExceptionHandler(RecursoEmConflitoException.class)
    public ResponseEntity<String> handleException(RecursoEmConflitoException e)  {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
}
