package com.example.sistemabibliotecaspring.controller;

import com.example.sistemabibliotecaspring.dto.emprestimoDTO.request.EmprestimoRequest;
import com.example.sistemabibliotecaspring.dto.emprestimoDTO.response.EmprestimoResponse;
import com.example.sistemabibliotecaspring.service.EmprestimosService;
import jakarta.validation.Valid;
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
    public Page<EmprestimoResponse> mostrarEmprestimosUsuario(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Boolean devolvido,
            Authentication authentication) {
        return emprestimosService.mostrarEmprestimosUsuario(authentication, page, size, devolvido);
    }

    @PostMapping("/emprestar")
    public ResponseEntity<EmprestimoResponse> emprestarLivro(
            Authentication authentication,
            @Valid @RequestBody EmprestimoRequest emprestimoRequest) {
        return emprestimosService.emprestarLivro(authentication, emprestimoRequest);
    }

    @PostMapping("/devolver")
    public ResponseEntity<EmprestimoResponse> devolverLivro(
            Authentication authentication,
            @Valid @RequestBody EmprestimoRequest emprestimoRequest) {
        return emprestimosService.devolverLivro(authentication, emprestimoRequest);
    }
}
