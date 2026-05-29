package com.example.sistemabiliotecaspring.repository;

import com.example.sistemabiliotecaspring.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuariosRepository extends JpaRepository<Usuario, Long> {
    public Usuario findByEmail(String email);
}
