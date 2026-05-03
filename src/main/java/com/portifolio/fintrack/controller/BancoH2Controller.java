package com.portifolio.fintrack.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.portifolio.fintrack.exception.RegraNegocioException;

@RestController
public class BancoH2Controller {

    private final JdbcTemplate jdbcTemplate;
    private final boolean h2ViewerEnabled;

    public BancoH2Controller(
            JdbcTemplate jdbcTemplate,
            @Value("${app.h2-viewer.enabled:true}") boolean h2ViewerEnabled
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.h2ViewerEnabled = h2ViewerEnabled;
    }

    @GetMapping("/banco-h2/tabelas")
    public List<String> listarTabelas() {
        validarAcesso();
        return jdbcTemplate.queryForList("""
                select table_name
                from information_schema.tables
                where table_schema = 'PUBLIC'
                order by table_name
                """, String.class);
    }

    @GetMapping("/banco-h2/dados")
    public List<Map<String, Object>> listarDados(@RequestParam String tabela) {
        validarAcesso();
        String tabelaNormalizada = tabela.toUpperCase();
        boolean existe = listarTabelas().contains(tabelaNormalizada);

        if (!tabelaNormalizada.matches("[A-Z0-9_]+") || !existe) {
            throw new RegraNegocioException("Tabela invalida.");
        }

        return jdbcTemplate.queryForList("select * from " + tabelaNormalizada + " limit 100");
    }

    private void validarAcesso() {
        if (!h2ViewerEnabled) {
            throw new RegraNegocioException("Visualizador H2 desativado neste ambiente.");
        }
    }
}
