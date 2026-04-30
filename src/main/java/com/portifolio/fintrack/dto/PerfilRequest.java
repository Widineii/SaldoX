package com.portifolio.fintrack.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PerfilRequest(
        @NotBlank(message = "O nome e obrigatorio")
        String nome,

        @NotBlank(message = "O email e obrigatorio")
        @Email(message = "Informe um email valido")
        String email,

        @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
        String senha
) {
}
