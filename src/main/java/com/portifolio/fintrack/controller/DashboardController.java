package com.portifolio.fintrack.controller;

import com.portifolio.fintrack.dto.ResumoFinanceiro;
import com.portifolio.fintrack.service.TransacaoService;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final TransacaoService service;

    public DashboardController(TransacaoService service) {
        this.service = service;
    }

    @GetMapping("/resumo")
    public ResumoFinanceiro resumo(@RequestParam @NotNull Long usuarioId) {
        return service.calcularResumo(usuarioId);
    }
}
