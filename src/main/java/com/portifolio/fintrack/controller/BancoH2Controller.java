package com.portifolio.fintrack.controller;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.portifolio.fintrack.exception.RegraNegocioException;

@RestController
public class BancoH2Controller {

    private final JdbcTemplate jdbcTemplate;

    public BancoH2Controller(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/banco-h2/tabelas")
    public List<String> listarTabelas() {
        return jdbcTemplate.queryForList("""
                select table_name
                from information_schema.tables
                where table_schema = 'PUBLIC'
                order by table_name
                """, String.class);
    }

    @GetMapping("/banco-h2/dados")
    public List<Map<String, Object>> listarDados(@RequestParam String tabela) {
        String tabelaNormalizada = tabela.toUpperCase();
        boolean existe = listarTabelas().contains(tabelaNormalizada);

        if (!tabelaNormalizada.matches("[A-Z0-9_]+") || !existe) {
            throw new RegraNegocioException("Tabela invalida.");
        }

        return jdbcTemplate.queryForList("select * from " + tabelaNormalizada + " limit 100");
    }
}
