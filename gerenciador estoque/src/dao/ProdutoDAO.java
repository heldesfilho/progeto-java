package dao;

import model.Produto;
import model.ResumoEstoque;
import util.ConexaoBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    // LEFT JOIN: produtos sem categoria também aparecem (categoria_nome vem null).
    private static final String SELECT_BASE =
            "SELECT p.id, p.codigo, p.nome, p.categoria_id, c.nome AS categoria_nome, p.preco_custo, "
          + "p.preco_venda, p.quantidade_atual, p.estoque_minimo, p.ativo "
          + "FROM produtos p LEFT JOIN categorias c ON c.id = p.categoria_id ";

    public void inserir(Produto p) throws SQLException {
        String sql = "INSERT INTO produtos (codigo, nome, categoria_id, preco_custo, preco_venda, estoque_minimo) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getCodigo());
            ps.setString(2, p.getNome());
            ps.setObject(3, p.getCategoriaId(), Types.INTEGER);
            ps.setBigDecimal(4, p.getPrecoCusto());
            ps.setBigDecimal(5, p.getPrecoVenda());
            ps.setInt(6, p.getEstoqueMinimo());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    p.setId(keys.getInt(1));
                }
            }
        }
    }

    // Não altera quantidade_atual: o saldo só muda por movimentação.
    public boolean atualizar(Produto p) throws SQLException {
        String sql = "UPDATE produtos SET codigo = ?, nome = ?, categoria_id = ?, "
                   + "preco_custo = ?, preco_venda = ?, estoque_minimo = ? WHERE id = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getCodigo());
            ps.setString(2, p.getNome());
            ps.setObject(3, p.getCategoriaId(), Types.INTEGER);
            ps.setBigDecimal(4, p.getPrecoCusto());
            ps.setBigDecimal(5, p.getPrecoVenda());
            ps.setInt(6, p.getEstoqueMinimo());
            ps.setInt(7, p.getId());
            return ps.executeUpdate() > 0;
        }
    }

    // "Exclusão" lógica: o produto some das listas, mas o histórico continua.
    public boolean desativar(int id) throws SQLException {
        String sql = "UPDATE produtos SET ativo = FALSE WHERE id = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public Produto buscarPorId(int id) throws SQLException {
        String sql = SELECT_BASE + "WHERE p.id = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    // Não filtra por ativo: o código continua "ocupado" mesmo se o produto foi desativado.
    public Produto buscarPorCodigo(String codigo) throws SQLException {
        String sql = SELECT_BASE + "WHERE p.codigo = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public List<Produto> listarAtivos() throws SQLException {
        String sql = SELECT_BASE + "WHERE p.ativo = TRUE ORDER BY p.nome";
        List<Produto> lista = new ArrayList<>();

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public List<Produto> buscarPorNome(String termo) throws SQLException {
        String sql = SELECT_BASE + "WHERE p.ativo = TRUE AND p.nome LIKE ? ORDER BY p.nome";
        List<Produto> lista = new ArrayList<>();

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + termo + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    // Produtos ativos com saldo igual ou abaixo do estoque mínimo.
    public List<Produto> listarEstoqueBaixo() throws SQLException {
        String sql = SELECT_BASE
                   + "WHERE p.ativo = TRUE AND p.quantidade_atual <= p.estoque_minimo ORDER BY p.nome";
        return consultarLista(sql, null);
    }

    // categoriaId null = produtos sem categoria.
    public List<Produto> listarPorCategoria(Integer categoriaId) throws SQLException {
        if (categoriaId == null) {
            return consultarLista(SELECT_BASE
                    + "WHERE p.ativo = TRUE AND p.categoria_id IS NULL ORDER BY p.nome", null);
        }
        return consultarLista(SELECT_BASE
                + "WHERE p.ativo = TRUE AND p.categoria_id = ? ORDER BY p.nome", categoriaId);
    }

    // Soma feita pelo próprio MySQL: é mais rápido do que trazer todos os produtos para o Java.
    public ResumoEstoque resumoEstoque() throws SQLException {
        String sql = "SELECT COUNT(*) AS produtos, "
                   + "COALESCE(SUM(quantidade_atual), 0) AS unidades, "
                   + "COALESCE(SUM(quantidade_atual * preco_custo), 0) AS valor_custo, "
                   + "COALESCE(SUM(quantidade_atual * preco_venda), 0) AS valor_venda "
                   + "FROM produtos WHERE ativo = TRUE";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return new ResumoEstoque(
                    rs.getInt("produtos"),
                    rs.getLong("unidades"),
                    rs.getBigDecimal("valor_custo"),
                    rs.getBigDecimal("valor_venda"));
        }
    }

    // Executa uma consulta que devolve produtos; o parâmetro (opcional) vai no primeiro "?".
    private List<Produto> consultarLista(String sql, Integer parametro) throws SQLException {
        List<Produto> lista = new ArrayList<>();

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (parametro != null) {
                ps.setInt(1, parametro);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    // Converte uma linha do banco em um objeto Produto.
    private Produto mapear(ResultSet rs) throws SQLException {
        Produto p = new Produto();
        p.setId(rs.getInt("id"));
        p.setCodigo(rs.getString("codigo"));
        p.setNome(rs.getString("nome"));

        int categoria = rs.getInt("categoria_id");
        p.setCategoriaId(rs.wasNull() ? null : categoria);
        p.setCategoriaNome(rs.getString("categoria_nome"));

        p.setPrecoCusto(rs.getBigDecimal("preco_custo"));
        p.setPrecoVenda(rs.getBigDecimal("preco_venda"));
        p.setQuantidadeAtual(rs.getInt("quantidade_atual"));
        p.setEstoqueMinimo(rs.getInt("estoque_minimo"));
        p.setAtivo(rs.getBoolean("ativo"));
        return p;
    }
}
