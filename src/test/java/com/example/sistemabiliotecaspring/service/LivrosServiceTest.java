package com.example.sistemabiliotecaspring.service;

import com.example.sistemabiliotecaspring.dto.livroDTO.LivroRequest;
import com.example.sistemabiliotecaspring.exception.RecursoNaoAutorizadoException;
import com.example.sistemabiliotecaspring.exception.RecursoNaoEncontradoException;
import com.example.sistemabiliotecaspring.model.Livro;
import com.example.sistemabiliotecaspring.repository.LivrosRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class LivrosServiceTest {
    @Mock
    private LivrosRepository livrosRepository;

    @InjectMocks
    private LivrosService livrosService;

    private final int PAGE = 0;
    private final int SIZE = 10;

    private final long ID = 1L;

    private final String TITULO = "Senhor dos Aneis";

    // getLivros()

    @Test
    public void getLivrosTestSemFiltro() {
        livrosService.getLivros(PAGE,SIZE,null,null);

        verify(livrosRepository).findAll(PageRequest.of(PAGE, SIZE));
    }

    @Test
    public void getLivrosTestComFiltroEmprestimoTrue() {
        livrosService.getLivros(PAGE,SIZE,null,true);

        verify(livrosRepository).findByEmprestado(true, PageRequest.of(PAGE, SIZE));
    }

    @Test
    public void getLivrosTestComFiltroEmprestimoFalse() {
        livrosService.getLivros(PAGE,SIZE,null,false);

        verify(livrosRepository).findByEmprestado(false, PageRequest.of(PAGE, SIZE));
    }

    @Test
    public void getLivrosTestComFiltroTitulo() {

        livrosService.getLivros(PAGE,SIZE,TITULO,null);

        verify(livrosRepository).findByNomeContainingIgnoreCase(TITULO,PageRequest.of(PAGE, SIZE));
    }

    @Test
    public void getLivrosTestComFiltroTituloEmprestimoTrue() {
        boolean ehEmprestado = true;

        livrosService.getLivros(PAGE,SIZE,TITULO,ehEmprestado);

        verify(livrosRepository).findByNomeContainingIgnoreCaseAndEmprestado(TITULO,ehEmprestado,PageRequest.of(PAGE, SIZE));
    }

    @Test
    public void getLivrosTestComFiltroTituloEmprestimoFalse() {
        boolean ehEmprestado = false;

        livrosService.getLivros(PAGE,SIZE,TITULO,ehEmprestado);

        verify(livrosRepository).findByNomeContainingIgnoreCaseAndEmprestado(TITULO,ehEmprestado,PageRequest.of(PAGE, SIZE));
    }



    // salvarLivro()

    @Test
    public void salvarLivroTestUsuarioNaoEhAdmin() {
        Authentication authentication = mock(Authentication.class);
        LivroRequest livroRequest = new LivroRequest();

        when(authentication.getAuthorities()).thenReturn(List.of());

        assertThrows(RecursoNaoAutorizadoException.class,
                () -> livrosService.salvarLivro(livroRequest, authentication));
    }

    @Test
    public void salvarLivroSemErros() {
        Authentication authentication = mock(Authentication.class);
        LivroRequest livroRequest = new LivroRequest(TITULO);

         when(authentication.getAuthorities())
                 .thenAnswer(invocation ->
                         List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        livrosService.salvarLivro(livroRequest, authentication);

        verify(livrosRepository).save(new Livro(livroRequest.getNome()));
    }



    // atualizarLivro()

    @Test
    public void atualizarLivroTestUsuarioNaoEhAdmin() {
        Authentication authentication = mock(Authentication.class);
        LivroRequest livroRequest = new LivroRequest();

        when(authentication.getAuthorities()).thenReturn(List.of());

        assertThrows(RecursoNaoAutorizadoException.class,
                () -> livrosService.atualizarLivro(ID, livroRequest, authentication));

    }

    @Test
    public void atualizarLivroTestLivroNaoEncontrado() {
        Authentication authentication = mock(Authentication.class);
        LivroRequest livroRequest = new LivroRequest();

        when(authentication.getAuthorities()).thenAnswer(invocation -> List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        when(livrosRepository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> livrosService.atualizarLivro(ID, livroRequest, authentication));

    }

    @Test
    public void atualizarLivroSucesso() {
        Authentication authentication = mock(Authentication.class);
        LivroRequest livroRequest = new LivroRequest(TITULO);

        when(authentication.getAuthorities()).thenAnswer(invocation -> List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        when(livrosRepository.findById(ID)).thenReturn(Optional.of(new Livro(ID, "Nome Antigo", false)));

        livrosService.atualizarLivro(ID, livroRequest, authentication);

        verify(livrosRepository).save(new Livro(ID, TITULO, false));
    }
}
