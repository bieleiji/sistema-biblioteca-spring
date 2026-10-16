package com.example.sistemabibliotecaspring.controller;

import com.example.sistemabibliotecaspring.dto.livroDTO.response.LivroResponse;
import com.example.sistemabibliotecaspring.model.Livro;
import com.example.sistemabibliotecaspring.dto.livroDTO.request.LivroRequest;
import com.example.sistemabibliotecaspring.service.LivrosService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/livros")
public class LivrosController {
    private final LivrosService livrosService;

    public LivrosController(LivrosService livrosService) {
        this.livrosService = livrosService;
    }

    @GetMapping
    public Page<LivroResponse> getLivros(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "titulo", required = false) String titulo,
            @RequestParam(name = "ehEmprestado", required = false) Boolean ehEmprestado) {
        return livrosService.getLivros(page, size, titulo, ehEmprestado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LivroResponse> getLivro(@PathVariable(name = "id") long id) {
        return livrosService.getLivro(id);
    }

    @PostMapping
    public ResponseEntity<Livro> salvarLivro(
            Authentication authentication,
            @Valid @RequestBody LivroRequest livroRequest) {
        return livrosService.salvarLivro(livroRequest, authentication);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Livro> atualizarLivro(
            Authentication authentication,
            @PathVariable(name = "id") long id,
            @Valid @RequestBody LivroRequest livroRequest) {
        return livrosService.atualizarLivro(id, livroRequest, authentication);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletarLivro(
            Authentication authentication,
            @PathVariable(name = "id") long id) {
        return livrosService.deletarLivro(id, authentication);
    }
}
