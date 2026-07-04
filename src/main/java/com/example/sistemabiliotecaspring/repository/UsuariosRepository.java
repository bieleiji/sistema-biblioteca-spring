package com.example.sistemabiliotecaspring.repository;

import com.example.sistemabiliotecaspring.model.Usuario;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuariosRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    @NullMarked Page<Usuario> findAll(Pageable pageable);
}
