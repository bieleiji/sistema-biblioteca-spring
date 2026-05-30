package com.example.sistemabiliotecaspring.repository;

import com.example.sistemabiliotecaspring.model.Livro;
import com.example.sistemabiliotecaspring.model.Usuario;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuariosRepository extends JpaRepository<Usuario, Long> {
    Usuario findByEmail(String email);
    Usuario findById(long id);
    @NullMarked Page<Usuario> findAll(Pageable pageable);
}
