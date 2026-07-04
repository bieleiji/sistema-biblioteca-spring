package com.example.sistemabiliotecaspring.service;

import com.example.sistemabiliotecaspring.dto.emprestimoDTO.EmprestimoRequest;
import com.example.sistemabiliotecaspring.exception.RecursoEmConflitoException;
import com.example.sistemabiliotecaspring.exception.RecursoNaoEncontradoException;
import com.example.sistemabiliotecaspring.model.Emprestimo;
import com.example.sistemabiliotecaspring.model.Livro;
import com.example.sistemabiliotecaspring.model.Usuario;
import com.example.sistemabiliotecaspring.repository.EmprestimosRepository;
import com.example.sistemabiliotecaspring.repository.LivrosRepository;
import com.example.sistemabiliotecaspring.repository.UsuariosRepository;
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

    public ResponseEntity<Object> emprestarLivro(Authentication authentication, EmprestimoRequest emprestimoRequest) {
        String email = authentication.getName();

        Usuario usuario = usuariosRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("usuario não encontrado"));

        Livro livro = livrosRepository.findById(emprestimoRequest.getId_livro())
                .orElseThrow(() -> new RecursoNaoEncontradoException("livro não encontrado"));

        if (livro.isEmprestado())
            throw new RecursoEmConflitoException("livro já foi emprestado");

        else {
            Emprestimo emprestimo = new Emprestimo();
            emprestimo.setUsuario(usuario);
            emprestimo.setLivro(livro);
            livro.setEmprestado(true);
            emprestimo.setData_emprestimo(LocalDate.now());
            emprestimo.setDevolvido(false);
            return ResponseEntity.status(HttpStatus.CREATED).body(emprestimosRepository.save(emprestimo));
        }
    }

        public ResponseEntity<Object> devolverLivro(Authentication authentication, EmprestimoRequest emprestimoRequest) {
        String email = authentication.getName();

        Usuario usuario = usuariosRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("usuario não encontrado"));

        Livro livro = livrosRepository.findById(emprestimoRequest.getId_livro())
                .orElseThrow(() -> new RecursoNaoEncontradoException("livro não encontrado"));

        Emprestimo emprestimo = emprestimosRepository.findEmprestimoByUsuarioAndLivroAndDevolvidoIsFalse(usuario,livro)
                .orElseThrow(() -> new RecursoNaoEncontradoException("emprestimo não encontrado"));

        if(emprestimo.isDevolvido())
            throw new RecursoEmConflitoException("livro já foi devolvido");

        else {
            emprestimo.setDate_devolucao(LocalDate.now());
            emprestimo.setDevolvido(true);
            livro.setEmprestado(false);
            return ResponseEntity.status(HttpStatus.OK).body(emprestimosRepository.save(emprestimo));
        }

    }

    public ResponseEntity<Page<Emprestimo>> mostrarEmprestimosUsuario(Authentication authentication, int page, int size, Boolean devolvido) {
        Usuario usuario = usuariosRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RecursoNaoEncontradoException("usuário não encontrado"));

        Pageable pageable = PageRequest.of(page, size);
        Page<Emprestimo> pagina;

        if(devolvido != null)
            pagina = emprestimosRepository.findEmprestimosByUsuarioAndDevolvido(usuario,devolvido,pageable);
        else
            pagina = emprestimosRepository.findEmprestimosByUsuario(usuario,pageable);
        return ResponseEntity.status(HttpStatus.OK).body(pagina);
    }
}
