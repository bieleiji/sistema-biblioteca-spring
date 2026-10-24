package com.example.sistemabibliotecaspring.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.sistemabibliotecaspring.exception.RecursoNaoAutenticadoException;
import com.example.sistemabibliotecaspring.model.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;


@Component
public class TokenService {
    protected final SecretKey secretKey;

    public TokenService(@Value("${jwt.secret-code}") String secret) {
        secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String gerarToken(Usuario usuario) {
        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim("Role", usuario.getRole().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(secretKey)
                .compact();
    }

    public Claims obterClaims(String token) {
        try {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        } catch (SignatureException e) {
            throw new RecursoNaoAutenticadoException("Token inválido", e);
        } catch (ExpiredJwtException e) {
            throw new RecursoNaoAutenticadoException("Token expirado", e);
        }
    }

    public String extrairSubject(String token) {
        return obterClaims(token).getSubject();
    }

    public boolean isTokenExpirado(String token) {
        return obterClaims(token).getExpiration().before(new Date());
    }
}
