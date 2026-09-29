package dao;
import model.AnaliseCategoria;
import model.Movimentacao;
import model.ResumoAnalise;
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
    public boolean registrar(Movimentacao m) throws SQLException {
        String sqlSaldo;
        if (m.getTipo() == TipoMovimentacao.ENTRADA) {
            sqlSaldo = "UPDATE produtos SET quantidade_atual = quantidade_atual + ? "
                     + "WHERE id = ? AND ativo = TRUE";
        } else {
            sqlSaldo = "UPDATE produtos SET quantidade_atual = quantidade_atual - ? "
                     + "WHERE id = ? AND ativo = TRUE AND quantidade_atual >= ?";
        }
        String sqlMov = "INSERT INTO movimentacoes (produto_id, tipo, quantidade, observacao) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexaoBD.getConexao()) {
            conn.setAutoCommit(false); 

            try (PreparedStatement psSaldo = conn.prepareStatement(sqlSaldo);
                 PreparedStatement psMov = conn.prepareStatement(sqlMov, Statement.RETURN_GENERATED_KEYS)) {

                psSaldo.setInt(1, m.getQuantidade());
                psSaldo.setInt(2, m.getProdutoId());
                if (m.getTipo() == TipoMovimentacao.SAIDA) {
                    psSaldo.setInt(3, m.getQuantidade());}
                if (psSaldo.executeUpdate() == 0) {
                    conn.rollback();
                    return false;}
                psMov.setInt(1, m.getProdutoId());
                psMov.setString(2, m.getTipo().name());
                psMov.setInt(3, m.getQuantidade());
                psMov.setString(4, m.getObservacao());
                psMov.executeUpdate();

                try (ResultSet keys = psMov.getGeneratedKeys()) {
                    if (keys.next()) {
                        m.setId(keys.getInt(1));}}

                conn.commit();           
                return true;

            } catch (SQLException e) {
                conn.rollback();         
                throw e;}}}

    public List<Movimentacao> listarPorProduto(int produtoId) throws SQLException {
        String sql = SELECT_BASE + "WHERE m.produto_id = ? ORDER BY m.data_hora DESC, m.id DESC";
        List<Movimentacao> lista = new ArrayList<>();

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, produtoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));}}}
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
                    lista.add(mapear(rs));}}}
        return lista;}
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
                    lista.add(mapear(rs));}}}
        return lista;}

    public ResumoAnalise resumoGeral(LocalDate inicio, LocalDate fim) throws SQLException {
        String sql = "SELECT "
                + "COALESCE(SUM(CASE WHEN m.tipo='ENTRADA' THEN m.quantidade ELSE 0 END), 0) AS total_entradas, "
                + "COALESCE(SUM(CASE WHEN m.tipo='SAIDA' THEN m.quantidade ELSE 0 END), 0) AS total_saidas, "
                + "COALESCE(SUM(CASE WHEN m.tipo='SAIDA' THEN m.quantidade * p.preco_venda ELSE 0 END), 0) AS faturamento, "
                + "COALESCE(SUM(CASE WHEN m.tipo='SAIDA' THEN m.quantidade * p.preco_custo ELSE 0 END), 0) AS custo_saidas, "
                + "COALESCE(SUM(CASE WHEN m.tipo='ENTRADA' THEN m.quantidade * p.preco_custo ELSE 0 END), 0) AS custo_entradas "
                + "FROM movimentacoes m JOIN produtos p ON p.id = m.produto_id "
                + "WHERE m.data_hora >= ? AND m.data_hora < ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(inicio.atStartOfDay()));
            ps.setTimestamp(2, Timestamp.valueOf(fim.plusDays(1).atStartOfDay()));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return new ResumoAnalise(
                        rs.getLong("total_entradas"),
                        rs.getLong("total_saidas"),
                        rs.getBigDecimal("faturamento"),
                        rs.getBigDecimal("custo_saidas"),
                        rs.getBigDecimal("custo_entradas"));}}}
    public List<AnaliseCategoria> analisePorCategoria(LocalDate inicio, LocalDate fim) throws SQLException {
        String sql = "SELECT COALESCE(c.nome, 'Sem categoria') AS categoria, "
                + "COALESCE(SUM(CASE WHEN m.tipo='ENTRADA' THEN m.quantidade ELSE 0 END), 0) AS unidades_entrada, "
                + "COALESCE(SUM(CASE WHEN m.tipo='SAIDA' THEN m.quantidade ELSE 0 END), 0) AS unidades_saida, "
                + "COALESCE(SUM(CASE WHEN m.tipo='SAIDA' THEN m.quantidade * p.preco_venda ELSE 0 END), 0) AS faturamento, "
                + "COALESCE(SUM(CASE WHEN m.tipo='SAIDA' THEN m.quantidade * p.preco_custo ELSE 0 END), 0) AS custo_saidas "
                + "FROM movimentacoes m "
                + "JOIN produtos p ON p.id = m.produto_id "
                + "LEFT JOIN categorias c ON c.id = p.categoria_id "
                + "WHERE m.data_hora >= ? AND m.data_hora < ? "
                + "GROUP BY categoria ORDER BY faturamento DESC";

        List<AnaliseCategoria> lista = new ArrayList<>();
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(inicio.atStartOfDay()));
            ps.setTimestamp(2, Timestamp.valueOf(fim.plusDays(1).atStartOfDay()));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new AnaliseCategoria(
                            rs.getString("categoria"),
                            rs.getLong("unidades_entrada"),
                            rs.getLong("unidades_saida"),
                            rs.getBigDecimal("faturamento"),
                            rs.getBigDecimal("custo_saidas")));}}}
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