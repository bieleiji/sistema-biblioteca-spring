package com.example.sistemabiliotecaspring.service;

import com.example.sistemabiliotecaspring.repository.LivrosRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LivrosServiceTest {
    @Mock
    private LivrosRepository livrosRepository;

    @InjectMocks
    private LivrosService livrosService;

    // getLivros()
    private final int PAGE = 0, SIZE = 10;

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
        String titulo = "Senhor dos Aneis";

        livrosService.getLivros(PAGE,SIZE,titulo,null);

        verify(livrosRepository).findByNomeContainingIgnoreCase(titulo,PageRequest.of(PAGE, SIZE));
    }

    @Test
    public void getLivrosTestComFiltroTituloEmprestimoTrue() {
        String titulo = "Senhor dos Aneis";
        boolean ehEmprestado = true;

        livrosService.getLivros(PAGE,SIZE,titulo,ehEmprestado);

        verify(livrosRepository).findByNomeContainingIgnoreCaseAndEmprestado(titulo,ehEmprestado,PageRequest.of(PAGE, SIZE));
    }

    @Test
    public void getLivrosTestComFiltroTituloEmprestimoFalse() {
        String titulo = "Senhor dos Aneis";
        boolean ehEmprestado = false;

        livrosService.getLivros(PAGE,SIZE,titulo,ehEmprestado);

        verify(livrosRepository).findByNomeContainingIgnoreCaseAndEmprestado(titulo,ehEmprestado,PageRequest.of(PAGE, SIZE));
    }

}
