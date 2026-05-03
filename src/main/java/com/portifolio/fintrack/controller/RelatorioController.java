package com.portifolio.fintrack.controller;

import com.portifolio.fintrack.model.TipoTransacao;
import com.portifolio.fintrack.service.TransacaoService;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@Validated
@RestController
@RequestMapping("/relatorios")
public class RelatorioController {

    private final TransacaoService transacaoService;

    public RelatorioController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @GetMapping(value = "/csv", produces = "text/csv")
    public ResponseEntity<String> exportarCsv(
            @RequestParam @NotNull Long usuarioId,
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) TipoTransacao tipo,
            @RequestParam(required = false) String mes
    ) {
        String csv = transacaoService.exportarCsv(usuarioId, busca, tipo, mes);

        return ResponseEntity.ok()
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename("saldox-relatorio.csv")
                        .build()
                        .toString())
                .body(csv);
    }
}
