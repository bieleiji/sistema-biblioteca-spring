package com.example.sistemabibliotecaspring.repository;

import com.example.sistemabibliotecaspring.model.Emprestimo;
import com.example.sistemabibliotecaspring.model.Livro;
import com.example.sistemabibliotecaspring.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmprestimosRepository extends JpaRepository<Emprestimo, Long> {
    Optional<Emprestimo> findEmprestimoByUsuarioAndLivroAndDevolvidoIsFalse(Usuario usuario, Livro livro);
    Page<Emprestimo> findEmprestimosByUsuario(Usuario usuario, Pageable pageable);
    Page<Emprestimo> findEmprestimosByUsuarioAndDevolvido(Usuario usuario, boolean devolvido, Pageable pageable);
}
