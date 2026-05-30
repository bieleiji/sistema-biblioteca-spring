package com.example.sistemabiliotecaspring.service;

import com.example.sistemabiliotecaspring.dto.EmprestimoRequest;
import com.example.sistemabiliotecaspring.model.Emprestimo;
import com.example.sistemabiliotecaspring.model.Livro;
import com.example.sistemabiliotecaspring.model.Usuario;
import com.example.sistemabiliotecaspring.repository.EmprestimosRepository;
import com.example.sistemabiliotecaspring.repository.LivrosRepository;
import com.example.sistemabiliotecaspring.repository.UsuariosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class EmprestimosService {
    @Autowired
    private EmprestimosRepository emprestimosRepository;

    @Autowired
    private LivrosRepository livrosRepository;

    @Autowired
    private UsuariosRepository usuariosRepository;

    public Emprestimo emprestarLivro(long id, EmprestimoRequest emprestimoRequest) {
        Usuario usuario = usuariosRepository.findById(id);
        Livro livro = livrosRepository.getLivroById(emprestimoRequest.getId_livro());

        if(usuario!=null && livro!=null) {
            Emprestimo emprestimo = new Emprestimo();
            emprestimo.setUsuario(usuario);
            emprestimo.setLivro(livro);
            livro.setEh_emprestado(true);
            emprestimo.setData_emprestimo(LocalDate.now());
            emprestimo.setDevolvido(false);
            return emprestimosRepository.save(emprestimo);
        }

        return null;
    }

    public Emprestimo devolverLivro(long id, EmprestimoRequest emprestimoRequest) {
        Usuario usuario = usuariosRepository.findById(id);
        Livro livro = livrosRepository.getLivroById(emprestimoRequest.getId_livro());

        if(usuario!=null &&  livro!=null) {
            Emprestimo emprestimo = emprestimosRepository.findEmprestimoByUsuarioAndLivro(usuario,livro);
            if(emprestimo!=null) {
                emprestimo.setDate_devolucao(LocalDate.now());
                emprestimo.setDevolvido(true);
                return emprestimosRepository.save(emprestimo);
            }
        }

        return null;
    }
}
