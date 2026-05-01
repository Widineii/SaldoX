package com.portifolio.fintrack.dto;

public record AuthResponse(
        Long usuarioId,
        String nome,
        String email,
        String token,
        String avatarUrl
) {
}
