package com.portifolio.fintrack.controller;

import com.portifolio.fintrack.model.TipoTransacao;
import com.portifolio.fintrack.service.TransacaoService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RelatorioControllerTest {

    private final TransacaoService service = mock(TransacaoService.class);
    private final RelatorioController controller = new RelatorioController(service);

    @Test
    void deveExportarCsvComCabecalhoDeDownload() {
        String csv = "Data,Descricao\n2026-05-01,Salario\n";
        when(service.exportarCsv(1L, "sal", TipoTransacao.RECEITA, "2026-05")).thenReturn(csv);

        ResponseEntity<String> response = controller.exportarCsv(1L, "sal", TipoTransacao.RECEITA, "2026-05");

        assertThat(response.getBody()).isEqualTo(csv);
        assertThat(response.getHeaders().getContentDisposition().getFilename()).isEqualTo("saldox-relatorio.csv");
        assertThat(response.getHeaders().getContentType().toString()).contains("text/csv");
        verify(service).exportarCsv(1L, "sal", TipoTransacao.RECEITA, "2026-05");
    }
}
