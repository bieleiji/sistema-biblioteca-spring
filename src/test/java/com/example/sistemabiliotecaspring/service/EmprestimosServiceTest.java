package com.example.sistemabiliotecaspring.service;

import com.example.sistemabiliotecaspring.dto.emprestimoDTO.EmprestimoRequest;
import com.example.sistemabiliotecaspring.exception.RecursoNaoEncontradoException;
import com.example.sistemabiliotecaspring.model.Emprestimo;
import com.example.sistemabiliotecaspring.model.Livro;
import com.example.sistemabiliotecaspring.model.Usuario;
import com.example.sistemabiliotecaspring.repository.EmprestimosRepository;
import com.example.sistemabiliotecaspring.repository.LivrosRepository;
import com.example.sistemabiliotecaspring.repository.UsuariosRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
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

    // emprestarLivro()

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

    @Test
    public void emprestarLivroSemErros() {
        Authentication authentication = mock(Authentication.class);
        EmprestimoRequest emprestimoRequest = new EmprestimoRequest(2L);
        String emailValido = "example@gmail.com";
        Usuario usuario = new Usuario(emailValido, "NomeTeste");
        Livro livro = new Livro(emprestimoRequest.getId_livro(), "LivroTeste", false);

        when(authentication.getName()).thenReturn(emailValido);
        when(usuariosRepository.findByEmail(emailValido)).thenReturn(Optional.of(usuario));
        when(livrosRepository.findById(emprestimoRequest.getId_livro())).thenReturn(Optional.of(livro));

        emprestimosService.emprestarLivro(authentication,emprestimoRequest);

        verify(emprestimosRepository).save(new Emprestimo(usuario, livro, LocalDate.now(), false));
    }

}
