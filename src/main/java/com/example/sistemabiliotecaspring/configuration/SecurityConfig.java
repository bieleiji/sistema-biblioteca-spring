package com.example.sistemabiliotecaspring.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthenticatorFilter jwtAuthenticatorFilter;

    public SecurityConfig(JwtAuthenticatorFilter jwtAuthenticatorFilter) {
        this.jwtAuthenticatorFilter = jwtAuthenticatorFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests -> {
                    authorizeRequests.requestMatchers(
                            "/usuarios/listar"
                            ).hasRole("ADMIN");

                    authorizeRequests.requestMatchers(
                            "/usuarios/criar_conta",
                                    "/usuarios/login"
                            ).permitAll()
                                .anyRequest().authenticated();
                })
                .httpBasic(httpBasic ->{})
                .addFilterBefore(jwtAuthenticatorFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
