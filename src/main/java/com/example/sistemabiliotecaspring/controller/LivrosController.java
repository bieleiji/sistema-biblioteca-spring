package com.example.sistemabiliotecaspring.controller;

import com.example.sistemabiliotecaspring.model.Livro;
import com.example.sistemabiliotecaspring.dto.LivroRequest;
import com.example.sistemabiliotecaspring.service.LivrosService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/livros")
public class LivrosController {
    private final LivrosService livrosService;

    public LivrosController(LivrosService livrosService) {
        this.livrosService = livrosService;
    }

    @GetMapping
    public Page<Livro> getLivros(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) Boolean ehEmprestado) {
        return livrosService.getLivros(page, size, titulo, ehEmprestado);
    }

    @PostMapping
    public Livro salvarLivro(@RequestBody LivroRequest livroRequest){
        return livrosService.salvarLivro(livroRequest);
    }

    @PatchMapping("/{id}")
    public Livro atualizarLivro(@PathVariable long id, @RequestBody LivroRequest livroRequest){
        return livrosService.atualizarLivro(id, livroRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletarLivro(@PathVariable long id){
        return livrosService.deletarLivro(id);
    }
}
