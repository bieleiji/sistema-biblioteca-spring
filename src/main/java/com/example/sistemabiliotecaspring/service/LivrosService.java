package com.example.sistemabiliotecaspring.service;

import com.example.sistemabiliotecaspring.model.Livro;
import com.example.sistemabiliotecaspring.repository.LivrosRepository;
import com.example.sistemabiliotecaspring.dto.LivroRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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

    public Livro salvarLivro(LivroRequest livroRequest) {
        Livro livroAdicionado = new Livro();
        livroAdicionado.setNome(livroRequest.getNome());
        livrosRepository.save(livroAdicionado);

        return livroAdicionado;
    }

    public Livro atualizarLivro(long id, LivroRequest livroRequest) {
        Livro livroAtualizado = livrosRepository.findById(id).orElse(null);

        if(livroAtualizado == null) return null;

        if(livroRequest.getNome() != null)
            if(!livroRequest.getNome().isBlank())
            livroAtualizado.setNome(livroRequest.getNome());

        if(livroRequest.isEh_emprestado() != livroAtualizado.isEmprestado())
            livroAtualizado.setEmprestado(livroRequest.isEh_emprestado());

        return livrosRepository.save(livroAtualizado);
    }

    public ResponseEntity<String> deletarLivro(long id) {
        if(livrosRepository.existsById(id))
            livrosRepository.deleteById(id);
        return ResponseEntity.status(200).body("Livro deletado com sucesso");
    }
}