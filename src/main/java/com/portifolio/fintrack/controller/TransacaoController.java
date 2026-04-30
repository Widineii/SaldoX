package com.portifolio.fintrack.controller;

import com.portifolio.fintrack.dto.TransacaoRequest;
import com.portifolio.fintrack.dto.TransacaoResponse;
import com.portifolio.fintrack.model.TipoTransacao;
import com.portifolio.fintrack.service.TransacaoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
    public TransacaoResponse criar(@Valid @RequestBody TransacaoRequest transacao) {
        return service.salvar(transacao);
    }

    @GetMapping
    public List<TransacaoResponse> listar(
            @RequestParam @NotNull Long usuarioId,
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) TipoTransacao tipo,
            @RequestParam(required = false) String mes
    ) {
        return service.listar(usuarioId, busca, tipo, mes);
    }

    @GetMapping("/{id}")
    public TransacaoResponse buscar(@PathVariable Long id, @RequestParam @NotNull Long usuarioId) {
        return service.buscarPorId(id, usuarioId);
    }

    @PutMapping("/{id}")
    public TransacaoResponse atualizar(@PathVariable Long id, @Valid @RequestBody TransacaoRequest transacao) {
        return service.atualizar(id, transacao);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id, @RequestParam @NotNull Long usuarioId) {
        service.deletar(id, usuarioId);
    }
}
