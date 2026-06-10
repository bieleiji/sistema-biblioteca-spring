package com.example.sistemabiliotecaspring.repository;

import com.example.sistemabiliotecaspring.model.Emprestimo;
import com.example.sistemabiliotecaspring.model.Livro;
import com.example.sistemabiliotecaspring.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmprestimosRepository extends JpaRepository<Emprestimo, Long> {
    Emprestimo findEmprestimoByUsuarioAndLivroAndDevolvidoIsFalse(Usuario usuario, Livro livro);
    Page<Emprestimo> findEmprestimosByUsuario(Usuario usuario, Pageable pageable);
    Page<Emprestimo> findEmprestimosByUsuarioAndDevolvido(Usuario usuario, boolean devolvido, Pageable pageable);
}
