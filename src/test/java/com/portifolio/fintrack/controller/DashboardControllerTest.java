package com.portifolio.fintrack.controller;

import com.portifolio.fintrack.dto.ResumoFinanceiro;
import com.portifolio.fintrack.service.TransacaoService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DashboardControllerTest {

    private final TransacaoService service = mock(TransacaoService.class);
    private final DashboardController controller = new DashboardController(service);

    @Test
    void deveRetornarResumoFinanceiro() {
        ResumoFinanceiro resumo = new ResumoFinanceiro(
                BigDecimal.valueOf(5000),
                BigDecimal.valueOf(1200),
                BigDecimal.valueOf(3800),
                2
        );
        when(service.calcularResumo(1L)).thenReturn(resumo);

        ResumoFinanceiro response = controller.resumo(1L);

        assertThat(response.saldo()).isEqualByComparingTo("3800");
        assertThat(response.quantidadeTransacoes()).isEqualTo(2);
        verify(service).calcularResumo(1L);
    }
}
