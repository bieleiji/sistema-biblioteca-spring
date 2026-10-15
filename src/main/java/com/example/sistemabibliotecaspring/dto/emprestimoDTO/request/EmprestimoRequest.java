package com.example.sistemabibliotecaspring.dto.emprestimoDTO.request;

import jakarta.validation.constraints.NotNull;

public record EmprestimoRequest(@NotNull Long id_livro) {

}
