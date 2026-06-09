package com.example.sistemabiliotecaspring.service;

import com.example.sistemabiliotecaspring.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;


@Component
public class TokenService {
    private final SecretKey secretKey;

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
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extrairSubject(String token) {
        return obterClaims(token).getSubject();
    }

    private boolean isTokenExpirado(String token) {
        return obterClaims(token).getExpiration().before(new Date());
    }

    public boolean ehTokenInvalido(String token, String email) {
        final String subjectToken = extrairSubject(token);
        return (!subjectToken.equals(email) || isTokenExpirado(token));
    }
}
