package com.example.sistemabiliotecaspring.service;

import com.example.sistemabiliotecaspring.dto.EmprestimoRequest;
import com.example.sistemabiliotecaspring.model.Emprestimo;
import com.example.sistemabiliotecaspring.model.Livro;
import com.example.sistemabiliotecaspring.model.Usuario;
import com.example.sistemabiliotecaspring.repository.EmprestimosRepository;
import com.example.sistemabiliotecaspring.repository.LivrosRepository;
import com.example.sistemabiliotecaspring.repository.UsuariosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class EmprestimosService {
    @Autowired
    private EmprestimosRepository emprestimosRepository;

    @Autowired
    private LivrosRepository livrosRepository;

    @Autowired
    private UsuariosRepository usuariosRepository;

    public ResponseEntity<Object> emprestarLivro(long id, EmprestimoRequest emprestimoRequest) {
        Usuario usuario = usuariosRepository.findById(id);
        Livro livro = livrosRepository.getLivroById(emprestimoRequest.getId_livro());

        if (usuario == null || livro == null || livro.isEmprestado()) {
            if(usuario == null)
                return ResponseEntity.status(404).body("usuario não encontrado");

            else if(livro == null)
                return ResponseEntity.status(404).body("livro não encontrado");

            else if(livro.isEmprestado())
                return ResponseEntity.status(403).body("livro já foi emprestado");

            else return null;
        } else {
            Emprestimo emprestimo = new Emprestimo();
            emprestimo.setUsuario(usuario);
            emprestimo.setLivro(livro);
            livro.setEmprestado(true);
            emprestimo.setData_emprestimo(LocalDate.now());
            emprestimo.setDevolvido(false);
            return ResponseEntity.status(201).body(emprestimosRepository.save(emprestimo));
        }
    }

    public ResponseEntity<Object> devolverLivro(long id, EmprestimoRequest emprestimoRequest) {
        Usuario usuario = usuariosRepository.findById(id);
        Livro livro = livrosRepository.getLivroById(emprestimoRequest.getId_livro());

        if (usuario == null || livro == null) {
            if(usuario == null)
                return ResponseEntity.status(404).body("usuario não encontrado");

            else return ResponseEntity.status(404).body("livro não encontrado");
        } else {
            Emprestimo emprestimo = emprestimosRepository.findEmprestimoByUsuarioAndLivroAndDevolvidoIsFalse(usuario,livro);
            if (emprestimo == null || emprestimo.isDevolvido()) {
                if (emprestimo != null) {
                    if(emprestimo.isDevolvido()) {
                        return ResponseEntity.status(403).body("livro já foi deovolvido");
                    }
                } else {
                    return ResponseEntity.status(404).body("emprestimo não encontrado");
                }
            } else {
                emprestimo.setDate_devolucao(LocalDate.now());
                emprestimo.setDevolvido(true);
                livro.setEmprestado(false);
                return ResponseEntity.status(201).body(emprestimosRepository.save(emprestimo));
            }
        }

        return null;
    }
}
