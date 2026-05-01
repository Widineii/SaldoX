package com.portifolio.fintrack.controller;

import com.portifolio.fintrack.dto.AuthRequest;
import com.portifolio.fintrack.dto.AuthResponse;
import com.portifolio.fintrack.dto.MensagemResponse;
import com.portifolio.fintrack.dto.RecuperacaoSenhaRequest;
import com.portifolio.fintrack.dto.RedefinirSenhaRequest;
import com.portifolio.fintrack.dto.RegistroRequest;
import com.portifolio.fintrack.service.AuthService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthControllerTest {

    private final AuthService authService = mock(AuthService.class);
    private final AuthController controller = new AuthController(authService);

    @Test
    void deveRegistrarUsuarioRetornandoJwt() {
        RegistroRequest request = new RegistroRequest("Ana", "ana@email.com", "123456");
        when(authService.registrar(request))
                .thenReturn(new AuthResponse(1L, "Ana", "ana@email.com", "jwt-token", null));

        AuthResponse response = controller.registrar(request);

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.usuarioId()).isEqualTo(1L);
        verify(authService).registrar(request);
    }

    @Test
    void deveDelegarLoginParaServico() {
        AuthRequest request = new AuthRequest("ana@email.com", "123456");
        when(authService.login(request))
                .thenReturn(new AuthResponse(1L, "Ana", "ana@email.com", "jwt-token", null));

        AuthResponse response = controller.login(request);

        assertThat(response.email()).isEqualTo("ana@email.com");
        assertThat(response.token()).isNotBlank();
    }

    @Test
    void deveGerarCodigoDeRecuperacaoEPermitirRedefinirSenha() {
        RecuperacaoSenhaRequest recuperacao = new RecuperacaoSenhaRequest("ana@email.com");
        RedefinirSenhaRequest redefinicao = new RedefinirSenhaRequest("ABC12345", "nova123");
        when(authService.solicitarRecuperacao(recuperacao))
                .thenReturn(new MensagemResponse("Codigo gerado", "ABC12345"));
        when(authService.redefinirSenha(redefinicao))
                .thenReturn(new MensagemResponse("Senha redefinida com sucesso.", null));

        assertThat(controller.recuperarSenha(recuperacao).token()).isEqualTo("ABC12345");
        assertThat(controller.redefinirSenha(redefinicao).mensagem()).isEqualTo("Senha redefinida com sucesso.");
    }
}
