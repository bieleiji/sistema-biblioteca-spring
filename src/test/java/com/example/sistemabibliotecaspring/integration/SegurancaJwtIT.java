package com.example.sistemabibliotecaspring.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Date;

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

    @BeforeEach
    void setUp() {
        usuariosRepository.deleteAll();

        usuariosRepository.save(
            new Usuario("Usuario Teste", "email@example.com", "senha", Role.USUARIO)
        );
    }

    @Test
    public void semTokenDeveRetornar401() throws Exception {
        mockMvc.perform(get("/livros"))
                .andExpect(status().isUnauthorized());
    }

    // resolver problema de teste não passando, depois de resolver problema em JwtAuthenticatorFilter
    
    @Test
    public void assinaturaInvalidaDeveRetornar401() throws Exception {
        TokenService tokenService = new TokenService("chave-secreta-inválida-com-mais-32-chars");
        
        String tokenInvalido = tokenService.gerarToken(usuariosRepository.findByEmail("email@example.com").get());
        
        mockMvc.perform(get("/livros")
                .header("Authorization", "Bearer " + tokenInvalido))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void tokenExpiradoDeveRetornar401() throws Exception {
        TokenService tokenService = new TokenService(secret) {
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
        String token = tokenService.gerarToken(usuariosRepository.findByEmail("email@example.com").get());

        mockMvc.perform(get("/livros")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void tokenValidoDeveRetornar200() throws Exception {
        mockMvc.perform(get("/livros")
    .header("Authorization", "Bearer " + tokenService.gerarToken(usuariosRepository.findByEmail("email@example.com").get())))
    .andExpect(status().isOk());
    }
}
