package com.example.sistemabibliotecaspring.service;

import com.example.sistemabibliotecaspring.dto.emprestimoDTO.EmprestimoRequest;
import com.example.sistemabibliotecaspring.exception.RecursoEmConflitoException;
import com.example.sistemabibliotecaspring.exception.RecursoNaoEncontradoException;
import com.example.sistemabibliotecaspring.model.Emprestimo;
import com.example.sistemabibliotecaspring.model.Livro;
import com.example.sistemabibliotecaspring.model.Usuario;
import com.example.sistemabibliotecaspring.repository.EmprestimosRepository;
import com.example.sistemabibliotecaspring.repository.LivrosRepository;
import com.example.sistemabibliotecaspring.repository.UsuariosRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
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

    // =================================================================================================================
    // emprestarLivro()
    // =================================================================================================================

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
    public void emprestarLivroTestLivroNaoEncontrado() {
        Authentication authentication = mock(Authentication.class);
        EmprestimoRequest emprestimoRequest = new EmprestimoRequest(-1L);
        String emailValido = "example@gmail.com";
        Usuario usuario = new Usuario(emailValido, "NomeTeste");

        when(authentication.getName()).thenReturn(emailValido);
        when(usuariosRepository.findByEmail(emailValido)).thenReturn(Optional.of(usuario));
        when(livrosRepository.findById(emprestimoRequest.getId_livro())).thenReturn(Optional.empty());


        assertThrows(RecursoNaoEncontradoException.class,
                () -> emprestimosService.emprestarLivro(authentication,emprestimoRequest));
    }

    @Test
    public void emprestarLivroTestLivroJaEmprestado() {
        Authentication authentication = mock(Authentication.class);
        EmprestimoRequest emprestimoRequest = new EmprestimoRequest(2L);
        String emailValido = "example@gmail.com";
        Usuario usuario = new Usuario(emailValido, "NomeTeste");
        Livro livro = new Livro(emprestimoRequest.getId_livro(), "LivroTeste", true);

        when(authentication.getName()).thenReturn(emailValido);
        when(usuariosRepository.findByEmail(emailValido)).thenReturn(Optional.of(usuario));
        when(livrosRepository.findById(emprestimoRequest.getId_livro())).thenReturn(Optional.of(livro));

        assertThrows(RecursoEmConflitoException.class,
                () -> emprestimosService.emprestarLivro(authentication,emprestimoRequest));
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

    // =================================================================================================================
    // devolverLivro()
    // =================================================================================================================

    @Test
    public void devolverLivroTestUsuarioNaoEncontrado() {
        Authentication authentication = mock(Authentication.class);
        EmprestimoRequest emprestimoRequest = new EmprestimoRequest(2L);
        String emailInvalido = "example@gmail.com";

        when(authentication.getName()).thenReturn(emailInvalido);
        when(usuariosRepository.findByEmail(emailInvalido)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> emprestimosService.devolverLivro(authentication,emprestimoRequest));
    }

    @Test
    public void devolverLivroTestLivroNaoEncontrado() {
        Authentication authentication = mock(Authentication.class);
        EmprestimoRequest emprestimoRequest = new EmprestimoRequest(2L);
        String emailValido = "example@gmail.com";
        Usuario usuario = new Usuario(emailValido, "NomeTeste");

        when(authentication.getName()).thenReturn(emailValido);
        when(usuariosRepository.findByEmail(emailValido)).thenReturn(Optional.of(usuario));
        when(livrosRepository.findById(emprestimoRequest.getId_livro())).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> emprestimosService.devolverLivro(authentication, emprestimoRequest));
    }

    @Test
    public void devolverLivroTestEmprestimoNaoEncontrado() {
        Authentication authentication = mock(Authentication.class);
        EmprestimoRequest emprestimoRequest = new EmprestimoRequest(2L);
        String emailValido = "example@gmail.com";
        Usuario usuario = new Usuario(emailValido, "NomeTeste");
        Livro livro = new Livro(emprestimoRequest.getId_livro(), "LivroTeste", true);

        when(authentication.getName()).thenReturn(emailValido);
        when(usuariosRepository.findByEmail(emailValido)).thenReturn(Optional.of(usuario));
        when(livrosRepository.findById(emprestimoRequest.getId_livro())).thenReturn(Optional.of(livro));
        when(emprestimosRepository.findEmprestimoByUsuarioAndLivroAndDevolvidoIsFalse(usuario,livro))
                .thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> emprestimosService.devolverLivro(authentication, emprestimoRequest));
    }

    @Test
    public void devolverLivroSemErros() {
        Authentication authentication = mock(Authentication.class);
        EmprestimoRequest emprestimoRequest = new EmprestimoRequest(2L);
        String emailValido = "example@gmail.com";
        Usuario usuario = new Usuario(emailValido, "NomeTeste");
        Livro livro = new Livro(emprestimoRequest.getId_livro(), "LivroTeste", true);
        Emprestimo emprestimo = new Emprestimo(usuario, livro, LocalDate.now(), false);

        when(authentication.getName()).thenReturn(emailValido);
        when(usuariosRepository.findByEmail(emailValido)).thenReturn(Optional.of(usuario));
        when(livrosRepository.findById(emprestimoRequest.getId_livro())).thenReturn(Optional.of(livro));
        when(emprestimosRepository.findEmprestimoByUsuarioAndLivroAndDevolvidoIsFalse(usuario,livro))
                .thenReturn(Optional.of(emprestimo));

        emprestimosService.devolverLivro(authentication, emprestimoRequest);

        verify(emprestimosRepository).save(emprestimo);

        assertTrue(emprestimo.isDevolvido());
        assertEquals(LocalDate.now(), emprestimo.getDate_devolucao());
        assertFalse(livro.isEmprestado());
    }

    // =================================================================================================================
    // mostrarEmprestimosUsuario()
    // =================================================================================================================

    @Test
    public void mostrarEmprestimosUsuarioTestUsuarioNaoEncontrado() {
        Authentication authentication = mock(Authentication.class);
        String emailInvalido = "emailinvalido@gmail.com";
        int page = 0, size = 10;
        Boolean devolvido = null;

        when(authentication.getName()).thenReturn(emailInvalido);
        when(usuariosRepository.findByEmail(emailInvalido)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> emprestimosService.mostrarEmprestimosUsuario(authentication, page, size, devolvido));
    }

    @Test
    public void mostrarEmprestimosUsuarioTestSemFiltro() {
        Authentication authentication = mock(Authentication.class);
        Usuario usuario = mock(Usuario.class);
        String emailValido = "emailinvalido@gmail.com";
        int page = 0, size = 10;
        Boolean devolvido = null;

        when(authentication.getName()).thenReturn(emailValido);
        when(usuariosRepository.findByEmail(emailValido)).thenReturn(Optional.of(usuario));

        emprestimosService.mostrarEmprestimosUsuario(authentication, page, size, devolvido);

        verify(emprestimosRepository).findEmprestimosByUsuario(usuario,PageRequest.of(page, size));
    }

    @Test
    public void mostrarEmprestimosUsuarioTestFiltroEmprestimosPendentes() {
        Authentication authentication = mock(Authentication.class);
        Usuario usuario = mock(Usuario.class);
        String emailValido = "emailinvalido@gmail.com";
        int page = 0, size = 10;
        boolean devolvido = false;

        when(authentication.getName()).thenReturn(emailValido);
        when(usuariosRepository.findByEmail(emailValido)).thenReturn(Optional.of(usuario));

        emprestimosService.mostrarEmprestimosUsuario(authentication, page, size, devolvido);

        verify(emprestimosRepository).findEmprestimosByUsuarioAndDevolvido(usuario,devolvido,PageRequest.of(page,size));
    }

    @Test
    public void mostrarEmprestimosUsuarioTestFiltroEmprestimosEmDia() {
        Authentication authentication = mock(Authentication.class);
        Usuario usuario = mock(Usuario.class);
        String emailValido = "emailinvalido@gmail.com";
        int page = 0, size = 10;
        boolean devolvido = true;

        when(authentication.getName()).thenReturn(emailValido);
        when(usuariosRepository.findByEmail(emailValido)).thenReturn(Optional.of(usuario));

        emprestimosService.mostrarEmprestimosUsuario(authentication, page, size, devolvido);

        verify(emprestimosRepository).findEmprestimosByUsuarioAndDevolvido(usuario,devolvido,PageRequest.of(page,size));
    }
}
