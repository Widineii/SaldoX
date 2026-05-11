package com.portifolio.fintrack.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final boolean h2ConsoleEnabled;
    private final boolean h2ViewerEnabled;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            @Value("${spring.h2.console.enabled:false}") boolean h2ConsoleEnabled,
            @Value("${app.h2-viewer.enabled:false}") boolean h2ViewerEnabled
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.h2ConsoleEnabled = h2ConsoleEnabled;
        this.h2ViewerEnabled = h2ViewerEnabled;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/", "/index.html", "/styles.css", "/app.js", "/swagger.html", "/openapi.yaml")
                            .permitAll();
                    if (h2ViewerEnabled) {
                        auth.requestMatchers("/banco-h2.html", "/banco-h2.js", "/banco-h2/**").permitAll();
                    } else {
                        auth.requestMatchers("/banco-h2.html", "/banco-h2.js", "/banco-h2/**").denyAll();
                    }
                    if (h2ConsoleEnabled) {
                        auth.requestMatchers("/h2-console/**").permitAll();
                    } else {
                        auth.requestMatchers("/h2-console/**").denyAll();
                    }
                    auth.requestMatchers(HttpMethod.POST, "/auth/registrar", "/auth/login", "/auth/recuperar-senha", "/auth/redefinir-senha")
                            .permitAll();
                    auth.anyRequest().authenticated();
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            throw new UsernameNotFoundException("Autenticacao feita pelo AuthService");
        };
    }
}
