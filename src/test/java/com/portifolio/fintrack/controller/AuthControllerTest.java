package com.portifolio.fintrack.controller;

import com.portifolio.fintrack.dto.AuthRequest;
import com.portifolio.fintrack.dto.AuthResponse;
import com.portifolio.fintrack.dto.MensagemResponse;
import com.portifolio.fintrack.dto.PerfilRequest;
import com.portifolio.fintrack.dto.RecuperacaoSenhaRequest;
import com.portifolio.fintrack.dto.RedefinirSenhaRequest;
import com.portifolio.fintrack.dto.RegistroRequest;
import com.portifolio.fintrack.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

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

    @Test
    void deveAtualizarPerfil() {
        PerfilRequest request = new PerfilRequest("Ana Maria", "ana.maria@email.com", null);
        when(authService.atualizarPerfil(1L, request))
                .thenReturn(new AuthResponse(1L, "Ana Maria", "ana.maria@email.com", "jwt-token", null));

        AuthResponse response = controller.atualizarPerfil(1L, request);

        assertThat(response.nome()).isEqualTo("Ana Maria");
        assertThat(response.email()).isEqualTo("ana.maria@email.com");
        verify(authService).atualizarPerfil(1L, request);
    }

    @Test
    void deveSalvarAvatar() {
        MultipartFile avatar = mock(MultipartFile.class);
        when(authService.salvarAvatar(1L, avatar))
                .thenReturn(new AuthResponse(1L, "Ana", "ana@email.com", "jwt-token", "data:image/png;base64,abc"));

        AuthResponse response = controller.salvarAvatar(1L, avatar);

        assertThat(response.avatarUrl()).startsWith("data:image/png");
        verify(authService).salvarAvatar(1L, avatar);
    }
}
