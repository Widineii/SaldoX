package com.portifolio.fintrack.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "A descricao e obrigatoria")
    private String descricao;

    @NotNull(message = "O valor e obrigatorio")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    private BigDecimal valor;

    @NotNull(message = "A data e obrigatoria")
    private LocalDate data;

    @NotBlank(message = "A categoria e obrigatoria")
    private String categoria;

    @NotNull(message = "O tipo e obrigatorio")
    @Enumerated(EnumType.STRING)
    private TipoTransacao tipo;

    private LocalDate vencimento;

    @Enumerated(EnumType.STRING)
    private StatusTransacao status = StatusTransacao.PAGA;

    private Integer parcelaAtual;

    private Integer totalParcelas;

    private Boolean cartaoCredito = false;

    @NotNull(message = "O usuario e obrigatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}
