package com.example.sistemabibliotecaspring.service;

import com.example.sistemabibliotecaspring.dto.livroDTO.response.LivroResponse;
import com.example.sistemabibliotecaspring.exception.RecursoNaoAutorizadoException;
import com.example.sistemabibliotecaspring.exception.RecursoNaoEncontradoException;
import com.example.sistemabibliotecaspring.model.Livro;
import com.example.sistemabibliotecaspring.repository.LivrosRepository;
import com.example.sistemabibliotecaspring.dto.livroDTO.request.LivroRequest;
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

    public Page<LivroResponse> getLivros(int page, int size, String titulo, Boolean ehEmprestado) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Livro> paginaOriginal;

        if(titulo != null && ehEmprestado != null)
            paginaOriginal = livrosRepository.findByNomeContainingIgnoreCaseAndEmprestado(titulo, ehEmprestado, pageable);

        else if(titulo != null)
            paginaOriginal = livrosRepository.findByNomeContainingIgnoreCase(titulo, pageable);

        else if(ehEmprestado != null)
            paginaOriginal = livrosRepository.findByEmprestado(ehEmprestado, pageable);

        else paginaOriginal = livrosRepository.findAll(pageable);

        return paginaOriginal.map(LivroResponse::new);
    }

    public ResponseEntity<LivroResponse> getLivro(long id) {
        Livro livro = livrosRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("livro não encontrado"));

        return ResponseEntity.status(HttpStatus.OK).body(new LivroResponse(livro));
    }

    public ResponseEntity<Livro> salvarLivro(LivroRequest livroRequest, Authentication authentication) {
        if(!authentication.getAuthorities().toString().contains("ROLE_ADMIN"))
            throw new RecursoNaoAutorizadoException("Apenas ADMINs podem acrescentar livros ao repositório");

        return ResponseEntity.status(HttpStatus.CREATED).body(livrosRepository.save(new Livro(livroRequest.getNome())));
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