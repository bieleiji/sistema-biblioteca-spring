package com.example.sistemabiliotecaspring.controller;

import com.example.sistemabiliotecaspring.dto.EmprestimoRequest;
import com.example.sistemabiliotecaspring.model.Emprestimo;
import com.example.sistemabiliotecaspring.service.EmprestimosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/emprestimos")
public class EmprestimosController {
    @Autowired
    private EmprestimosService emprestimosService;

    @GetMapping
    public ResponseEntity<Page<Emprestimo>> mostrarEmprestimosUsuario(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Boolean devolvido,
            Authentication authentication) {
        return emprestimosService.mostrarEmprestimosUsuario(authentication, page, size, devolvido);
    }

    @PostMapping("/emprestar")
    public ResponseEntity<Object> emprestarLivro(
            Authentication authentication,
            @RequestBody EmprestimoRequest emprestimoRequest) {
        return emprestimosService.emprestarLivro(authentication, emprestimoRequest);
    }

    @PostMapping("/devolver")
    public ResponseEntity<Object> devolverLivro(
            Authentication authentication,
            @RequestBody EmprestimoRequest emprestimoRequest) {
        return emprestimosService.devolverLivro(authentication, emprestimoRequest);
    }
}
