package com.portifolio.fintrack.dto;

import com.portifolio.fintrack.model.StatusTransacao;
import com.portifolio.fintrack.model.TipoTransacao;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransacaoResponse(
        Long id,
        String descricao,
        BigDecimal valor,
        LocalDate data,
        String categoria,
        TipoTransacao tipo,
        LocalDate vencimento,
        StatusTransacao status,
        Integer parcelaAtual,
        Integer totalParcelas,
        Boolean cartaoCredito,
        Long usuarioId
) {
}
