package com.example.sistemabiliotecaspring.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.boot.context.properties.bind.DefaultValue;

@Entity(name = "livros")
public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_livro")
    private Long id;

    @NotBlank
    private String nome;

    private boolean eh_emprestado;

    public Livro() {}

    public Livro(String nome, boolean eh_emprestado) {
        this.nome = nome;
        this.eh_emprestado = eh_emprestado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean isEh_emprestado() {
        return eh_emprestado;
    }

    public void setEh_emprestado(boolean eh_emprestado) {
        this.eh_emprestado = eh_emprestado;
    }
}
