package com.example.sistemabiliotecaspring.repository;

import com.example.sistemabiliotecaspring.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuariosRepository extends JpaRepository<Usuario, Long> {
    public Usuario findByEmail(String email);
    public Usuario findById(long id);
}
