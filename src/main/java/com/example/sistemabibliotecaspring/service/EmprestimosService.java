package com.example.sistemabibliotecaspring.service;

import com.example.sistemabibliotecaspring.dto.emprestimoDTO.request.EmprestimoRequest;
import com.example.sistemabibliotecaspring.dto.emprestimoDTO.response.EmprestimoResponse;
import com.example.sistemabibliotecaspring.exception.RecursoEmConflitoException;
import com.example.sistemabibliotecaspring.exception.RecursoNaoEncontradoException;
import com.example.sistemabibliotecaspring.model.Emprestimo;
import com.example.sistemabibliotecaspring.model.Livro;
import com.example.sistemabibliotecaspring.model.Usuario;
import com.example.sistemabibliotecaspring.repository.EmprestimosRepository;
import com.example.sistemabibliotecaspring.repository.LivrosRepository;
import com.example.sistemabibliotecaspring.repository.UsuariosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    public Page<EmprestimoResponse> mostrarEmprestimosUsuario(Authentication authentication, int page, int size, Boolean devolvido) {
        Usuario usuario = usuariosRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RecursoNaoEncontradoException("usuário não encontrado"));

        Pageable pageable = PageRequest.of(page, size);
        Page<Emprestimo> paginaOriginal;

        if(devolvido != null)
            paginaOriginal = emprestimosRepository.findEmprestimosByUsuarioAndDevolvido(usuario,devolvido,pageable);
        else
            paginaOriginal = emprestimosRepository.findEmprestimosByUsuario(usuario,pageable);

        return paginaOriginal.map(EmprestimoResponse::new);
    }

    public ResponseEntity<EmprestimoResponse> emprestarLivro(Authentication authentication, EmprestimoRequest emprestimoRequest) {
        String email = authentication.getName();

        Usuario usuario = usuariosRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("usuario não encontrado"));

        Livro livro = livrosRepository.findById(emprestimoRequest.id_livro())
                .orElseThrow(() -> new RecursoNaoEncontradoException("livro não encontrado"));

        if (livro.isEmprestado())
            throw new RecursoEmConflitoException("livro já foi emprestado");

        else {
            livro.setEmprestado(true);
            return ResponseEntity.status(HttpStatus.CREATED).body(new EmprestimoResponse(
                    emprestimosRepository.save(new Emprestimo(usuario, livro, LocalDate.now(), false))
            ));
        }
    }

        public ResponseEntity<EmprestimoResponse> devolverLivro(Authentication authentication, EmprestimoRequest emprestimoRequest) {
        String email = authentication.getName();

        Usuario usuario = usuariosRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("usuario não encontrado"));

        Livro livro = livrosRepository.findById(emprestimoRequest.id_livro())
                .orElseThrow(() -> new RecursoNaoEncontradoException("livro não encontrado"));

        Emprestimo emprestimo = emprestimosRepository
                .findEmprestimoByUsuarioAndLivroAndDevolvidoIsFalse(usuario,livro)
                .orElseThrow(() -> new RecursoNaoEncontradoException("emprestimo não encontrado"));

        emprestimo.setData_devolucao(LocalDate.now());
        emprestimo.setDevolvido(true);
        livro.setEmprestado(false);
        return ResponseEntity.status(HttpStatus.OK).body(
                new EmprestimoResponse(emprestimosRepository.save(emprestimo)));
    }
}
