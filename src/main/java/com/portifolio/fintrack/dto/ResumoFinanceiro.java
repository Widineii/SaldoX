package com.portifolio.fintrack.dto;

import java.math.BigDecimal;

public record ResumoFinanceiro(
        BigDecimal totalReceitas,
        BigDecimal totalDespesas,
        BigDecimal saldo,
        long quantidadeTransacoes
) {
}
