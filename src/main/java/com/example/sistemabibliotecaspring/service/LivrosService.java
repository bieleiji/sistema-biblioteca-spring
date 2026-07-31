package com.example.sistemabibliotecaspring.service;

import com.example.sistemabibliotecaspring.exception.RecursoNaoAutorizadoException;
import com.example.sistemabibliotecaspring.exception.RecursoNaoEncontradoException;
import com.example.sistemabibliotecaspring.model.Livro;
import com.example.sistemabibliotecaspring.repository.LivrosRepository;
import com.example.sistemabibliotecaspring.dto.livroDTO.LivroRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class LivrosService {
    private final LivrosRepository livrosRepository;

    public LivrosService(LivrosRepository livrosRepository)  {
        this.livrosRepository = livrosRepository;
    }

    public Page<Livro> getLivros(int page, int size, String titulo, Boolean ehEmprestado) {
        Pageable pageable = PageRequest.of(page, size);

        if(titulo != null && ehEmprestado != null)
            return livrosRepository.findByNomeContainingIgnoreCaseAndEmprestado(titulo, ehEmprestado, pageable);

        if(titulo != null)
            return livrosRepository.findByNomeContainingIgnoreCase(titulo, pageable);

        if(ehEmprestado != null)
            return livrosRepository.findByEmprestado(ehEmprestado, pageable);

        return livrosRepository.findAll(pageable);
    }

    public ResponseEntity<Livro> salvarLivro(LivroRequest livroRequest, Authentication authentication) {
        if(!authentication.getAuthorities().toString().contains("ROLE_ADMIN"))
            throw new RecursoNaoAutorizadoException("Apenas ADMINs podem acrescentar livros ao repositório");

        return ResponseEntity.status(HttpStatus.OK).body(livrosRepository.save(new Livro(livroRequest.getNome())));
    }

    public ResponseEntity<Livro> atualizarLivro(long id, LivroRequest livroRequest, Authentication authentication) {
        if(!authentication.getAuthorities().toString().contains("ROLE_ADMIN"))
            throw new RecursoNaoAutorizadoException("Apenas ADMINs podem alterar livros no repositório");


        Livro livroAtualizado = livrosRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("livro não encontrado"));

        livroAtualizado.setNome(livroRequest.getNome());

        if(livroRequest.isEh_emprestado() != livroAtualizado.isEmprestado())
            livroAtualizado.setEmprestado(livroRequest.isEh_emprestado());

        return ResponseEntity.status(HttpStatus.OK).body(livrosRepository.save(livroAtualizado));
    }

    public ResponseEntity<String> deletarLivro(long id, Authentication authentication) {
        if(!authentication.getAuthorities().toString().contains("ROLE_ADMIN"))
            throw new RecursoNaoAutorizadoException("Apenas ADMINs podem deletar livros no repositório");

        if(!livrosRepository.existsById(id))
            throw new RecursoNaoEncontradoException("livro não encontrado");
        else
            livrosRepository.deleteById(id);

        return ResponseEntity.status(HttpStatus.OK).body("Livro deletado com sucesso");
    }
}