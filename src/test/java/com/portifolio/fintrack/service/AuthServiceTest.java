package com.portifolio.fintrack.service;

import com.portifolio.fintrack.dto.AuthRequest;
import com.portifolio.fintrack.dto.RegistroRequest;
import com.portifolio.fintrack.exception.RegraNegocioException;
import com.portifolio.fintrack.repository.UsuarioRepository;
import com.portifolio.fintrack.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    private final UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
    private final JwtService jwtService = new JwtService(new ObjectMapper(), "segredo-de-teste", 8);
    private final AuthService authService = new AuthService(usuarioRepository, new BCryptPasswordEncoder(), jwtService);

    @Test
    void deveBloquearRegistroComEmailDuplicado() {
        when(usuarioRepository.existsByEmail("teste@email.com")).thenReturn(true);

        RegistroRequest request = new RegistroRequest("Teste", "teste@email.com", "123456");

        assertThatThrownBy(() -> authService.registrar(request))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("Este email ja esta cadastrado");
    }

    @Test
    void deveValidarSenhaCriptografada() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String senhaCriptografada = encoder.encode("123456");

        assertThat(encoder.matches("123456", senhaCriptografada)).isTrue();
    }

    @Test
    void deveBloquearLoginInvalido() {
        AuthRequest request = new AuthRequest("naoexiste@email.com", "123456");

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("Email ou senha invalidos");
    }
}
