package com.portifolio.fintrack.repository;

import com.portifolio.fintrack.model.TipoTransacao;
import com.portifolio.fintrack.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    @Query("""
            select t from Transacao t
            where t.usuario.id = :usuarioId
              and (:busca is null or lower(t.descricao) like lower(concat('%', :busca, '%'))
                   or lower(t.categoria) like lower(concat('%', :busca, '%')))
              and (:tipo is null or t.tipo = :tipo)
              and (:inicio is null or t.data >= :inicio)
              and (:fim is null or t.data <= :fim)
            order by t.data desc, t.id desc
            """)
    List<Transacao> filtrar(
            @Param("usuarioId") Long usuarioId,
            @Param("busca") String busca,
            @Param("tipo") TipoTransacao tipo,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim
    );

    List<Transacao> findAllByUsuarioIdOrderByDataDescIdDesc(Long usuarioId);
}
