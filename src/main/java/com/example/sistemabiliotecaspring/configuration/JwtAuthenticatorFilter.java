package com.example.sistemabiliotecaspring.configuration;

import com.example.sistemabiliotecaspring.exception.RecursoNaoAutenticadoException;
import com.example.sistemabiliotecaspring.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticatorFilter extends OncePerRequestFilter {
    private final TokenService tokenService;
    private final UsuarioDetailsService usuarioDetailsService;

    public JwtAuthenticatorFilter(TokenService tokenService, UsuarioDetailsService usuarioDetailsService) {
        this.tokenService = tokenService;
        this.usuarioDetailsService = usuarioDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");

        if(token == null || !token.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        token = token.replace("Bearer ", "");

        if(!tokenService.isTokenExpirado(token)) {
            String email = tokenService.extrairSubject(token);
            UserDetails userDetails = usuarioDetailsService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        } else {
            throw new RecursoNaoAutenticadoException("token invalido");
        }

        filterChain.doFilter(request,response);
    }
}
