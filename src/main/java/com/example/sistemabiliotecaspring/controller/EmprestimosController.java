package com.example.sistemabiliotecaspring.controller;

import com.example.sistemabiliotecaspring.dto.EmprestimoRequest;
import com.example.sistemabiliotecaspring.service.EmprestimosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/emprestimos")
public class EmprestimosController {
    private final EmprestimosService emprestimosService;
    public EmprestimosController(EmprestimosService emprestimosService) {
        this.emprestimosService = emprestimosService;
    }

    @PostMapping("/{id}/emprestar")
    public ResponseEntity<Object> emprestarLivro(@PathVariable long id, @RequestBody EmprestimoRequest emprestimoRequest) {
        return emprestimosService.emprestarLivro(id, emprestimoRequest);
    }

    @PostMapping("/{id}/devolver")
    public ResponseEntity<Object> devolverLivro(@PathVariable long id, @RequestBody EmprestimoRequest emprestimoRequest) {
        return emprestimosService.devolverLivro(id, emprestimoRequest);
    }
}
