package com.portifolio.fintrack.dto;

import com.portifolio.fintrack.model.TipoTransacao;
import com.portifolio.fintrack.model.StatusTransacao;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransacaoRequest(
        @NotBlank(message = "A descricao e obrigatoria")
        String descricao,

        @NotNull(message = "O valor e obrigatorio")
        @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
        BigDecimal valor,

        @NotNull(message = "A data e obrigatoria")
        LocalDate data,

        @NotBlank(message = "A categoria e obrigatoria")
        String categoria,

        @NotNull(message = "O tipo e obrigatorio")
        TipoTransacao tipo,

        LocalDate vencimento,

        StatusTransacao status,

        Integer parcelaAtual,

        Integer totalParcelas,

        Boolean cartaoCredito,

        @NotNull(message = "O usuarioId e obrigatorio")
        Long usuarioId
) {
}
