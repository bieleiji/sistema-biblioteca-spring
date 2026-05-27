package com.example.sistemabiliotecaspring.controller;

import com.example.sistemabiliotecaspring.entities.Livro;
import com.example.sistemabiliotecaspring.request.LivroRequest;
import com.example.sistemabiliotecaspring.service.LivrosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/livros")
public class LivrosController {
    private final LivrosService livrosService;

    public LivrosController(LivrosService livrosService) {
        this.livrosService = livrosService;
    }

    @GetMapping
    public List<Livro> getLivros(){
        return livrosService.getLivros();
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
