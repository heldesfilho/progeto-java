package service;

import dao.UsuarioDAO;
import model.Cargo;
import model.Usuario;
import util.SenhaUtil;

import java.sql.SQLException;

public class UsuarioService {

    private final UsuarioDAO dao = new UsuarioDAO();

    public Usuario criarFuncionario(Usuario solicitante, String usuario, String senha, Cargo cargo)
            throws SQLException {

        if (solicitante == null || solicitante.getCargo() != Cargo.GERENTE) {
            throw new IllegalArgumentException("Apenas o gerente pode criar novos usuários.");}
        if (cargo == null) {
            throw new IllegalArgumentException("Informe o cargo do novo usuário.");}
        return inserirUsuario(usuario, senha, cargo);}

    public Usuario autenticar(String usuario, String senha) throws SQLException {
        Usuario u = dao.buscarPorUsuario(validarUsuario(usuario));
        if (u == null) return null;
        return SenhaUtil.confere(senha, u.getSenhaSalt(), u.getSenhaHash()) ? u : null;
    }

    private Usuario inserirUsuario(String usuario, String senha, Cargo cargo) throws SQLException {
        String nome = validarUsuario(usuario);
        if (senha == null || senha.length() < 4) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 4 caracteres.");
        }
        if (dao.buscarPorUsuario(nome) != null) {
            throw new IllegalArgumentException("Esse nome de usuário já está em uso.");}

        String salt = SenhaUtil.gerarSalt();
        String hash = SenhaUtil.hash(senha, salt);
        Usuario u = new Usuario(nome, hash, salt, cargo);

        try {
            dao.inserir(u);
        } catch (SQLException e) {
            if (isViolacaoDeUnicidade(e)) {
                throw new IllegalArgumentException("Esse nome de usuário já está em uso.");
            }
            throw e;
        }
        return u;
    }

    private String validarUsuario(String usuario) {
        if (usuario == null || usuario.isBlank()) {
            throw new IllegalArgumentException("O nome de usuário é obrigatório.");
        }
        return usuario.trim();
    }

    private boolean isViolacaoDeUnicidade(SQLException e) {
        return e.getSQLState() != null && e.getSQLState().startsWith("23"); 
    }
}