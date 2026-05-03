package com.portifolio.fintrack.service;

import com.portifolio.fintrack.dto.ResumoFinanceiro;
import com.portifolio.fintrack.dto.TransacaoRequest;
import com.portifolio.fintrack.dto.TransacaoResponse;
import com.portifolio.fintrack.model.StatusTransacao;
import com.portifolio.fintrack.model.TipoTransacao;
import com.portifolio.fintrack.model.Usuario;
import com.portifolio.fintrack.repository.TransacaoRepository;
import com.portifolio.fintrack.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class TransacaoServiceIntegrationTest {

    @Autowired
    private TransacaoService transacaoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Test
    void deveSalvarFiltrarResumirEExportarCsvComH2() {
        Usuario usuario = criarUsuario();

        transacaoService.salvar(new TransacaoRequest(
                "Salario Maio",
                BigDecimal.valueOf(5200),
                LocalDate.of(2026, 5, 1),
                "Trabalho",
                TipoTransacao.RECEITA,
                null,
                StatusTransacao.PAGA,
                null,
                null,
                false,
                usuario.getId()
        ));
        transacaoService.salvar(new TransacaoRequest(
                "Celular parcelado",
                BigDecimal.valueOf(440),
                LocalDate.of(2026, 5, 3),
                "Eletronicos",
                TipoTransacao.DESPESA,
                LocalDate.of(2026, 5, 10),
                StatusTransacao.PENDENTE,
                1,
                12,
                true,
                usuario.getId()
        ));

        List<TransacaoResponse> despesas = transacaoService.listar(usuario.getId(), "celular", TipoTransacao.DESPESA, "2026-05");
        ResumoFinanceiro resumo = transacaoService.calcularResumo(usuario.getId());
        String csv = transacaoService.exportarCsv(usuario.getId(), null, null, "2026-05");

        assertThat(despesas).hasSize(1);
        assertThat(despesas.get(0).descricao()).isEqualTo("Celular parcelado");
        assertThat(resumo.totalReceitas()).isEqualByComparingTo("5200");
        assertThat(resumo.totalDespesas()).isEqualByComparingTo("440");
        assertThat(resumo.saldo()).isEqualByComparingTo("4760");
        assertThat(resumo.quantidadeTransacoes()).isEqualTo(2);
        assertThat(csv).contains("Data,Descricao,Categoria,Tipo,Status,Vencimento,Parcela,Valor");
        assertThat(csv).contains("\"Celular parcelado\"");
        assertThat(transacaoRepository.findAllByUsuarioIdOrderByDataDescIdDesc(usuario.getId())).hasSize(2);
    }

    private Usuario criarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setNome("Teste Integracao");
        usuario.setEmail("integracao-" + System.nanoTime() + "@email.com");
        usuario.setSenha("senha");
        return usuarioRepository.save(usuario);
    }
}
