package com.example.sistemabibliotecaspring.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Date;
import java.util.Optional;

import com.example.sistemabibliotecaspring.exception.RecursoNaoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.example.sistemabibliotecaspring.model.Role;
import com.example.sistemabibliotecaspring.model.Usuario;
import com.example.sistemabibliotecaspring.repository.UsuariosRepository;
import com.example.sistemabibliotecaspring.service.TokenService;

import io.jsonwebtoken.Jwts;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles ("test")
public class SegurancaJwtIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private UsuariosRepository usuariosRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;


    @Value ("${jwt.secret-code}") String secret;

    private void adicionarUsuarioTeste(Role role) {
        usuariosRepository.save(
                new Usuario(
                        "Usuario Teste",
                        "email@example.com",
                        passwordEncoder.encode("senha"),
                        role
                )
        );
    }

    @BeforeEach
    void setUp() {
        usuariosRepository.deleteAll();

        adicionarUsuarioTeste(Role.USUARIO);
    }

    private String getTokenUsuarioTeste(TokenService tokenService) {
        Optional<Usuario> usuario = usuariosRepository.findByEmail("email@example.com");
        if(usuario.isPresent()) return tokenService.gerarToken(usuario.get());
        else throw new RecursoNaoEncontradoException("Usuário não encontrado");
    }

    @Test
    void semTokenDeveRetornar401() throws Exception {
        mockMvc.perform(get("/livros"))
                .andExpect(status().isUnauthorized());
    }

    // resolver problema de teste não passando, depois de resolver problema em JwtAuthenticatorFilter
    
    @Test
    void assinaturaInvalidaDeveRetornar401() throws Exception {
        TokenService tokenServiceChaveInvalida = new TokenService("chave-secreta-inválida-com-mais-32-chars");
        
        String tokenInvalido = getTokenUsuarioTeste(tokenServiceChaveInvalida);
        
        mockMvc.perform(get("/livros")
                .header("Authorization", "Bearer " + tokenInvalido))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenExpiradoDeveRetornar401() throws Exception {
        TokenService tokenServiceTokenExpirado = new TokenService(secret) {
            @Override
            public String gerarToken(Usuario usuario) {
                return Jwts.builder()
                        .subject(usuario.getEmail())
                        .claim("Role", usuario.getRole().toString())
                        .issuedAt(new Date(System.currentTimeMillis() - 86400000)) // 1 dia atrás
                        .expiration(new Date(System.currentTimeMillis() - 3600000)) // 1 hora atrás
                        .signWith(secretKey)
                        .compact();
            }
        };
        String token = getTokenUsuarioTeste(tokenServiceTokenExpirado);

        mockMvc.perform(get("/livros")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenValidoUsuarioExcluido401() throws Exception {
        String tokenUsuarioExcluido = getTokenUsuarioTeste(tokenService);
        usuariosRepository.deleteAll();

        mockMvc.perform(get("/livros")
                .header("Authorization", "Bearer " + tokenUsuarioExcluido))
                .andExpect(status().isUnauthorized());

        adicionarUsuarioTeste(Role.USUARIO);
    }

    @Test
    void usuarioNaoAutorizadoDeveRetornar403() throws Exception {
        mockMvc.perform(get("/usuarios/listar")
                .header("Authorization", "Bearer " + getTokenUsuarioTeste(tokenService)))
                .andExpect(status().isForbidden());
    }

    @Test
    void usuarioAdminAutorizadoDeveRetornar200() throws Exception {
        usuariosRepository.deleteAll();
        adicionarUsuarioTeste(Role.ADMIN);

        mockMvc.perform(get("/usuarios/listar")
                        .header("Authorization", "Bearer " + getTokenUsuarioTeste(tokenService)))
                .andExpect(status().isOk());
    }

    @Test
    void tokenValidoDeveRetornar200() throws Exception {
        mockMvc.perform(get("/livros")
    .header("Authorization", "Bearer " + getTokenUsuarioTeste(tokenService)))
    .andExpect(status().isOk());
    }
}
