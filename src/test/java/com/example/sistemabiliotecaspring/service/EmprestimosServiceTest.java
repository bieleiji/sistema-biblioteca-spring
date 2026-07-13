package com.example.sistemabiliotecaspring.service;

import com.example.sistemabiliotecaspring.dto.emprestimoDTO.EmprestimoRequest;
import com.example.sistemabiliotecaspring.exception.RecursoNaoEncontradoException;
import com.example.sistemabiliotecaspring.repository.EmprestimosRepository;
import com.example.sistemabiliotecaspring.repository.LivrosRepository;
import com.example.sistemabiliotecaspring.repository.UsuariosRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class EmprestimosServiceTest {
    @Mock
    private EmprestimosRepository emprestimosRepository;

    @Mock
    private LivrosRepository livrosRepository;

    @Mock
    private UsuariosRepository usuariosRepository;

    @InjectMocks
    private EmprestimosService emprestimosService;

    @Test
    public void emprestarLivroTestUsuarioNaoEncontrado() {
        Authentication authentication = mock(Authentication.class);
        EmprestimoRequest emprestimoRequest = new EmprestimoRequest(2L);
        String emailInvalido = "example@gmail.com";

        when(authentication.getName()).thenReturn(emailInvalido);
        when(usuariosRepository.findByEmail(emailInvalido)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> emprestimosService.emprestarLivro(authentication, emprestimoRequest));
    }



}
