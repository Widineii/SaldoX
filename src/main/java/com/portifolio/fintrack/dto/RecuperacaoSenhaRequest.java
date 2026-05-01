package com.portifolio.fintrack.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RecuperacaoSenhaRequest(
        @NotBlank(message = "O email e obrigatorio")
        @Email(message = "Informe um email valido")
        String email
) {
}
