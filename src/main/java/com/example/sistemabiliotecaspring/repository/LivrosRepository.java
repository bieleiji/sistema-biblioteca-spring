package com.example.sistemabiliotecaspring.repository;

import com.example.sistemabiliotecaspring.entities.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface  LivrosRepository extends JpaRepository<Livro, Long> {
    public Livro getLivroById(Long id);
}
