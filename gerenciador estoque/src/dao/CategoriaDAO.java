package dao;

import model.Categoria;
import util.ConexaoBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    public void inserir(Categoria c) throws SQLException {
        String sql = "INSERT INTO categorias (nome) VALUES (?)";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, c.getNome());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    c.setId(keys.getInt(1));
                }
            }
        }
    }

    public boolean atualizar(Categoria c) throws SQLException {
        String sql = "UPDATE categorias SET nome = ? WHERE id = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getNome());
            ps.setInt(2, c.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean remover(int id) throws SQLException {
        String sql = "DELETE FROM categorias WHERE id = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public Categoria buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nome FROM categorias WHERE id = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    // Busca pelo nome exato. O MySQL ignora maiúsculas/minúsculas e acentos nessa comparação
    // (por causa do collation utf8mb4_unicode_ci), então "limpeza" e "Limpeza" são o mesmo nome.
    public Categoria buscarPorNome(String nome) throws SQLException {
        String sql = "SELECT id, nome FROM categorias WHERE nome = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nome);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public List<Categoria> listar() throws SQLException {
        String sql = "SELECT id, nome FROM categorias ORDER BY nome";
        List<Categoria> lista = new ArrayList<>();

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    // Conta TODOS os produtos da categoria, inclusive os desativados.
    public int contarProdutos(int categoriaId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM produtos WHERE categoria_id = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoriaId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    private Categoria mapear(ResultSet rs) throws SQLException {
        return new Categoria(rs.getInt("id"), rs.getString("nome"));
    }
}
