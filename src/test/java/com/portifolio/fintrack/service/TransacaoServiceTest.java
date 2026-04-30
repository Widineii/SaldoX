package com.portifolio.fintrack.service;

import com.portifolio.fintrack.dto.TransacaoRequest;
import com.portifolio.fintrack.exception.RecursoNaoEncontradoException;
import com.portifolio.fintrack.model.TipoTransacao;
import com.portifolio.fintrack.model.Transacao;
import com.portifolio.fintrack.model.Usuario;
import com.portifolio.fintrack.repository.TransacaoRepository;
import com.portifolio.fintrack.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TransacaoServiceTest {

    private final TransacaoRepository transacaoRepository = mock(TransacaoRepository.class);
    private final UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
    private final TransacaoService service = new TransacaoService(transacaoRepository, usuarioRepository);

    @Test
    void deveCalcularResumoDoUsuario() {
        when(transacaoRepository.findAllByUsuarioIdOrderByDataDescIdDesc(1L))
                .thenReturn(List.of(
                        transacao(TipoTransacao.RECEITA, "1000.00"),
                        transacao(TipoTransacao.DESPESA, "250.00")
                ));

        var resumo = service.calcularResumo(1L);

        assertThat(resumo.totalReceitas()).isEqualByComparingTo("1000.00");
        assertThat(resumo.totalDespesas()).isEqualByComparingTo("250.00");
        assertThat(resumo.saldo()).isEqualByComparingTo("750.00");
        assertThat(resumo.quantidadeTransacoes()).isEqualTo(2);
    }

    @Test
    void deveSalvarTransacaoComUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Lucas");
        usuario.setEmail("lucas@email.com");
        usuario.setSenha("senha");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(transacaoRepository.save(any(Transacao.class))).thenAnswer(invocation -> {
            Transacao transacao = invocation.getArgument(0);
            transacao.setId(10L);
            return transacao;
        });

        TransacaoRequest request = new TransacaoRequest(
                "Salario",
                new BigDecimal("1000.00"),
                LocalDate.now(),
                "Trabalho",
                TipoTransacao.RECEITA,
                null,
                null,
                null,
                null,
                false,
                1L
        );

        var response = service.salvar(request);

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.usuarioId()).isEqualTo(1L);
        assertThat(response.tipo()).isEqualTo(TipoTransacao.RECEITA);
    }

    @Test
    void deveFalharQuandoUsuarioNaoExiste() {
        TransacaoRequest request = new TransacaoRequest(
                "Salario",
                new BigDecimal("1000.00"),
                LocalDate.now(),
                "Trabalho",
                TipoTransacao.RECEITA,
                null,
                null,
                null,
                null,
                false,
                99L
        );

        assertThatThrownBy(() -> service.salvar(request))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Usuario nao encontrado");
    }

    private Transacao transacao(TipoTransacao tipo, String valor) {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        Transacao transacao = new Transacao();
        transacao.setUsuario(usuario);
        transacao.setDescricao("Teste");
        transacao.setCategoria("Teste");
        transacao.setData(LocalDate.now());
        transacao.setTipo(tipo);
        transacao.setValor(new BigDecimal(valor));
        return transacao;
    }
}
