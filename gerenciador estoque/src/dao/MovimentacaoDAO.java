package dao;

import model.Movimentacao;
import model.TipoMovimentacao;
import util.ConexaoBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MovimentacaoDAO {

    private static final String SELECT_BASE =
            "SELECT m.id, m.produto_id, p.nome AS produto_nome, m.tipo, m.quantidade, m.data_hora, m.observacao "
          + "FROM movimentacoes m JOIN produtos p ON p.id = m.produto_id ";

    /**
     * Registra a movimentação E atualiza o saldo do produto na MESMA transação:
     * ou as duas coisas acontecem, ou nenhuma.
     *
     * Devolve false se o saldo não pôde ser atualizado (produto inativo ou, numa saída,
     * saldo insuficiente); nesse caso nada é gravado.
     */
    public boolean registrar(Movimentacao m) throws SQLException {
        String sqlSaldo;
        if (m.getTipo() == TipoMovimentacao.ENTRADA) {
            sqlSaldo = "UPDATE produtos SET quantidade_atual = quantidade_atual + ? "
                     + "WHERE id = ? AND ativo = TRUE";
        } else {
            // A condição "quantidade_atual >= ?" garante que o saldo nunca fique negativo,
            // mesmo que outra operação tenha alterado o produto no meio tempo.
            sqlSaldo = "UPDATE produtos SET quantidade_atual = quantidade_atual - ? "
                     + "WHERE id = ? AND ativo = TRUE AND quantidade_atual >= ?";
        }
        String sqlMov = "INSERT INTO movimentacoes (produto_id, tipo, quantidade, observacao) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexaoBD.getConexao()) {
            conn.setAutoCommit(false);   // inicia a transação

            try (PreparedStatement psSaldo = conn.prepareStatement(sqlSaldo);
                 PreparedStatement psMov = conn.prepareStatement(sqlMov, Statement.RETURN_GENERATED_KEYS)) {

                psSaldo.setInt(1, m.getQuantidade());
                psSaldo.setInt(2, m.getProdutoId());
                if (m.getTipo() == TipoMovimentacao.SAIDA) {
                    psSaldo.setInt(3, m.getQuantidade());
                }

                if (psSaldo.executeUpdate() == 0) {
                    conn.rollback();
                    return false;
                }

                psMov.setInt(1, m.getProdutoId());
                psMov.setString(2, m.getTipo().name());
                psMov.setInt(3, m.getQuantidade());
                psMov.setString(4, m.getObservacao());
                psMov.executeUpdate();

                try (ResultSet keys = psMov.getGeneratedKeys()) {
                    if (keys.next()) {
                        m.setId(keys.getInt(1));
                    }
                }

                conn.commit();           // confirma as duas alterações juntas
                return true;

            } catch (SQLException e) {
                conn.rollback();         // desfaz tudo se algo falhar
                throw e;
            }
        }
    }

    public List<Movimentacao> listarPorProduto(int produtoId) throws SQLException {
        String sql = SELECT_BASE + "WHERE m.produto_id = ? ORDER BY m.data_hora DESC, m.id DESC";
        List<Movimentacao> lista = new ArrayList<>();

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, produtoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    public List<Movimentacao> listarRecentes(int limite) throws SQLException {
        String sql = SELECT_BASE + "ORDER BY m.data_hora DESC, m.id DESC LIMIT ?";
        List<Movimentacao> lista = new ArrayList<>();

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    // Inclui o dia inicial e o dia final inteiros: de 00:00 do início até 00:00 do dia seguinte ao fim.
    public List<Movimentacao> listarPorPeriodo(LocalDate inicio, LocalDate fim) throws SQLException {
        String sql = SELECT_BASE
                   + "WHERE m.data_hora >= ? AND m.data_hora < ? ORDER BY m.data_hora DESC, m.id DESC";
        List<Movimentacao> lista = new ArrayList<>();

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(inicio.atStartOfDay()));
            ps.setTimestamp(2, Timestamp.valueOf(fim.plusDays(1).atStartOfDay()));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    private Movimentacao mapear(ResultSet rs) throws SQLException {
        Movimentacao m = new Movimentacao();
        m.setId(rs.getInt("id"));
        m.setProdutoId(rs.getInt("produto_id"));
        m.setProdutoNome(rs.getString("produto_nome"));
        m.setTipo(TipoMovimentacao.valueOf(rs.getString("tipo")));
        m.setQuantidade(rs.getInt("quantidade"));
        m.setDataHora(rs.getTimestamp("data_hora").toLocalDateTime());
        m.setObservacao(rs.getString("observacao"));
        return m;
    }
}
