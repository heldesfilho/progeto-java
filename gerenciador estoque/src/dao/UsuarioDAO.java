package dao;

import model.Cargo;
import model.Usuario;
import util.ConexaoBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    public void inserir(Usuario u) throws SQLException {
        String sql = "INSERT INTO usuarios (usuario, senha_hash, senha_salt, cargo) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getUsuario());
            ps.setString(2, u.getSenhaHash());
            ps.setString(3, u.getSenhaSalt());
            ps.setString(4, u.getCargo().name());
            ps.executeUpdate();}}
    public Usuario buscarPorUsuario(String usuario) throws SQLException {
        String sql = "SELECT id, usuario, senha_hash, senha_salt, cargo FROM usuarios WHERE usuario = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setUsuario(rs.getString("usuario"));
                u.setSenhaHash(rs.getString("senha_hash"));
                u.setSenhaSalt(rs.getString("senha_salt"));
                u.setCargo(Cargo.valueOf(rs.getString("cargo")));
                return u;}}}
}