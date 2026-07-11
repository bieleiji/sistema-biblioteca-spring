package com.example.sistemabiliotecaspring.service;

import com.example.sistemabiliotecaspring.repository.EmprestimosRepository;
import com.example.sistemabiliotecaspring.repository.LivrosRepository;
import com.example.sistemabiliotecaspring.repository.UsuariosRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

}
