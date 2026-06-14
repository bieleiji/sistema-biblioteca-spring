package com.example.sistemabiliotecaspring.controller;

import com.example.sistemabiliotecaspring.dto.EmprestimoRequest;
import com.example.sistemabiliotecaspring.model.Emprestimo;
import com.example.sistemabiliotecaspring.service.EmprestimosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
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
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return emprestimosService.mostrarEmprestimosUsuario(token, page, size, devolvido);
    }

    @PostMapping("/emprestar")
    public ResponseEntity<Object> emprestarLivro(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody EmprestimoRequest emprestimoRequest) {
        String token = authHeader.replace("Bearer ", "");
        return emprestimosService.emprestarLivro(token, emprestimoRequest);
    }

    @PostMapping("/devolver")
    public ResponseEntity<Object> devolverLivro(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody EmprestimoRequest emprestimoRequest) {
        String token = authHeader.replace("Bearer ", "");
        return emprestimosService.devolverLivro(token, emprestimoRequest);
    }
}
