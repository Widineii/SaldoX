package com.portifolio.fintrack.service;

import com.portifolio.fintrack.dto.ResumoFinanceiro;
import com.portifolio.fintrack.dto.TransacaoRequest;
import com.portifolio.fintrack.dto.TransacaoResponse;
import com.portifolio.fintrack.exception.RecursoNaoEncontradoException;
import com.portifolio.fintrack.model.TipoTransacao;
import com.portifolio.fintrack.model.StatusTransacao;
import com.portifolio.fintrack.model.Transacao;
import com.portifolio.fintrack.model.Usuario;
import com.portifolio.fintrack.repository.TransacaoRepository;
import com.portifolio.fintrack.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public TransacaoService(TransacaoRepository transacaoRepository, UsuarioRepository usuarioRepository) {
        this.transacaoRepository = transacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public TransacaoResponse salvar(TransacaoRequest request) {
        Usuario usuario = buscarUsuario(request.usuarioId());

        Transacao transacao = new Transacao();
        preencherTransacao(transacao, request, usuario);

        return toResponse(transacaoRepository.save(transacao));
    }

    public List<TransacaoResponse> listar(Long usuarioId, String busca, TipoTransacao tipo, String mes) {
        Periodo periodo = montarPeriodo(mes);

        return transacaoRepository.filtrar(
                        usuarioId,
                        limparBusca(busca),
                        tipo,
                        periodo.inicio(),
                        periodo.fim()
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public TransacaoResponse buscarPorId(Long id, Long usuarioId) {
        return toResponse(buscarTransacaoDoUsuario(id, usuarioId));
    }

    public TransacaoResponse atualizar(Long id, TransacaoRequest request) {
        Transacao transacao = buscarTransacaoDoUsuario(id, request.usuarioId());
        Usuario usuario = buscarUsuario(request.usuarioId());
        preencherTransacao(transacao, request, usuario);

        return toResponse(transacaoRepository.save(transacao));
    }

    public void deletar(Long id, Long usuarioId) {
        Transacao transacao = buscarTransacaoDoUsuario(id, usuarioId);
        transacaoRepository.delete(transacao);
    }

    public ResumoFinanceiro calcularResumo(Long usuarioId) {
        List<Transacao> transacoes = transacaoRepository.findAllByUsuarioIdOrderByDataDescIdDesc(usuarioId);

        BigDecimal receitas = somarPorTipo(transacoes, TipoTransacao.RECEITA);
        BigDecimal despesas = somarPorTipo(transacoes, TipoTransacao.DESPESA);
        BigDecimal saldo = receitas.subtract(despesas);

        return new ResumoFinanceiro(receitas, despesas, saldo, transacoes.size());
    }

    private void preencherTransacao(Transacao transacao, TransacaoRequest request, Usuario usuario) {
        transacao.setDescricao(request.descricao());
        transacao.setValor(request.valor());
        transacao.setData(request.data());
        transacao.setCategoria(request.categoria());
        transacao.setTipo(request.tipo());
        transacao.setVencimento(request.vencimento());
        transacao.setStatus(normalizarStatus(request));
        transacao.setParcelaAtual(request.parcelaAtual());
        transacao.setTotalParcelas(request.totalParcelas());
        transacao.setCartaoCredito(Boolean.TRUE.equals(request.cartaoCredito()));
        transacao.setUsuario(usuario);
    }

    private StatusTransacao normalizarStatus(TransacaoRequest request) {
        if (request.status() != null) {
            return request.status();
        }

        if (request.tipo() == TipoTransacao.DESPESA && request.vencimento() != null) {
            return request.vencimento().isBefore(LocalDate.now()) ? StatusTransacao.ATRASADA : StatusTransacao.PENDENTE;
        }

        return StatusTransacao.PAGA;
    }

    private Transacao buscarTransacaoDoUsuario(Long id, Long usuarioId) {
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Transacao nao encontrada"));

        if (!transacao.getUsuario().getId().equals(usuarioId)) {
            throw new RecursoNaoEncontradoException("Transacao nao encontrada para este usuario");
        }

        return transacao;
    }

    private Usuario buscarUsuario(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado"));
    }

    private BigDecimal somarPorTipo(List<Transacao> transacoes, TipoTransacao tipo) {
        return transacoes.stream()
                .filter(transacao -> transacao.getTipo() == tipo)
                .map(Transacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private TransacaoResponse toResponse(Transacao transacao) {
        return new TransacaoResponse(
                transacao.getId(),
                transacao.getDescricao(),
                transacao.getValor(),
                transacao.getData(),
                transacao.getCategoria(),
                transacao.getTipo(),
                transacao.getVencimento(),
                statusAtualizado(transacao),
                transacao.getParcelaAtual(),
                transacao.getTotalParcelas(),
                Boolean.TRUE.equals(transacao.getCartaoCredito()),
                transacao.getUsuario().getId()
        );
    }

    private StatusTransacao statusAtualizado(Transacao transacao) {
        if (transacao.getStatus() == StatusTransacao.PENDENTE
                && transacao.getVencimento() != null
                && transacao.getVencimento().isBefore(LocalDate.now())) {
            return StatusTransacao.ATRASADA;
        }

        return transacao.getStatus() == null ? StatusTransacao.PAGA : transacao.getStatus();
    }

    private String limparBusca(String busca) {
        if (busca == null || busca.isBlank()) {
            return null;
        }

        return busca.trim();
    }

    private Periodo montarPeriodo(String mes) {
        if (mes == null || mes.isBlank()) {
            return new Periodo(null, null);
        }

        YearMonth yearMonth = YearMonth.parse(mes);
        return new Periodo(yearMonth.atDay(1), yearMonth.atEndOfMonth());
    }

    private record Periodo(LocalDate inicio, LocalDate fim) {
    }
}
