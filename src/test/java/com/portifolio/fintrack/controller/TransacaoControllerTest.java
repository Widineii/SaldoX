package com.portifolio.fintrack.controller;

import com.portifolio.fintrack.dto.TransacaoRequest;
import com.portifolio.fintrack.dto.TransacaoResponse;
import com.portifolio.fintrack.model.StatusTransacao;
import com.portifolio.fintrack.model.TipoTransacao;
import com.portifolio.fintrack.service.JwtService.JwtUsuario;
import com.portifolio.fintrack.service.TransacaoService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransacaoControllerTest {

    private static final JwtUsuario USUARIO = new JwtUsuario(1L, "ana@email.com");

    private final TransacaoService service = mock(TransacaoService.class);
    private final TransacaoController controller = new TransacaoController(service);

    @Test
    void deveCriarTransacao() {
        TransacaoRequest request = request();
        TransacaoResponse response = response();
        when(service.salvar(1L, request)).thenReturn(response);

        TransacaoResponse criada = controller.criar(USUARIO, request);

        assertThat(criada.descricao()).isEqualTo("Salario");
        assertThat(criada.tipo()).isEqualTo(TipoTransacao.RECEITA);
        verify(service).salvar(1L, request);
    }

    @Test
    void deveListarTransacoesDoUsuario() {
        when(service.listar(1L, null, null, null)).thenReturn(List.of(response()));

        List<TransacaoResponse> transacoes = controller.listar(USUARIO, null, null, null);

        assertThat(transacoes).hasSize(1);
        assertThat(transacoes.get(0).usuarioId()).isEqualTo(1L);
        verify(service).listar(1L, null, null, null);
    }

    @Test
    void deveBuscarTransacaoPorId() {
        when(service.buscarPorId(10L, 1L)).thenReturn(response());

        TransacaoResponse encontrada = controller.buscar(USUARIO, 10L);

        assertThat(encontrada.id()).isEqualTo(10L);
        verify(service).buscarPorId(10L, 1L);
    }

    @Test
    void deveAtualizarTransacao() {
        TransacaoRequest request = request();
        when(service.atualizar(10L, 1L, request)).thenReturn(response());

        TransacaoResponse atualizada = controller.atualizar(USUARIO, 10L, request);

        assertThat(atualizada.descricao()).isEqualTo("Salario");
        verify(service).atualizar(10L, 1L, request);
    }

    @Test
    void deveDeletarTransacao() {
        controller.deletar(USUARIO, 10L);

        verify(service).deletar(10L, 1L);
    }

    private TransacaoRequest request() {
        return new TransacaoRequest(
                "Salario",
                BigDecimal.valueOf(5000),
                LocalDate.of(2026, 5, 1),
                "Trabalho",
                TipoTransacao.RECEITA,
                null,
                StatusTransacao.PAGA,
                null,
                null,
                false
        );
    }

    private TransacaoResponse response() {
        return new TransacaoResponse(
                10L,
                "Salario",
                BigDecimal.valueOf(5000),
                LocalDate.of(2026, 5, 1),
                "Trabalho",
                TipoTransacao.RECEITA,
                null,
                StatusTransacao.PAGA,
                null,
                null,
                false,
                1L
        );
    }
}
