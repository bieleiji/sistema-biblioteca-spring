package com.example.sistemabiliotecaspring.repository;

import com.example.sistemabiliotecaspring.model.Livro;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LivrosRepository extends JpaRepository<Livro, Long> {
    @NullMarked Page<Livro> findAll(Pageable pageable);
    Page<Livro> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
    Page<Livro> findByEmprestado(boolean emprestado, Pageable pageable);
    Page<Livro> findByNomeContainingIgnoreCaseAndEmprestado(String nome, Boolean emprestado, Pageable pageable);
}
