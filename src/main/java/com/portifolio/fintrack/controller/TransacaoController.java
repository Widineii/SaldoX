package com.portifolio.fintrack.controller;

import com.portifolio.fintrack.dto.TransacaoRequest;
import com.portifolio.fintrack.dto.TransacaoResponse;
import com.portifolio.fintrack.model.TipoTransacao;
import com.portifolio.fintrack.service.JwtService.JwtUsuario;
import com.portifolio.fintrack.service.TransacaoService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/transacoes")
@CrossOrigin(origins = "*")
public class TransacaoController {

    private final TransacaoService service;

    public TransacaoController(TransacaoService service) {
        this.service = service;
    }

    @PostMapping
    public TransacaoResponse criar(@AuthenticationPrincipal JwtUsuario usuario, @Valid @RequestBody TransacaoRequest transacao) {
        return service.salvar(usuario.usuarioId(), transacao);
    }

    @GetMapping
    public List<TransacaoResponse> listar(
            @AuthenticationPrincipal JwtUsuario usuario,
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) TipoTransacao tipo,
            @RequestParam(required = false) String mes
    ) {
        return service.listar(usuario.usuarioId(), busca, tipo, mes);
    }

    @GetMapping("/{id}")
    public TransacaoResponse buscar(@AuthenticationPrincipal JwtUsuario usuario, @PathVariable Long id) {
        return service.buscarPorId(id, usuario.usuarioId());
    }

    @PutMapping("/{id}")
    public TransacaoResponse atualizar(
            @AuthenticationPrincipal JwtUsuario usuario,
            @PathVariable Long id,
            @Valid @RequestBody TransacaoRequest transacao
    ) {
        return service.atualizar(id, usuario.usuarioId(), transacao);
    }

    @DeleteMapping("/{id}")
    public void deletar(@AuthenticationPrincipal JwtUsuario usuario, @PathVariable Long id) {
        service.deletar(id, usuario.usuarioId());
    }
}
