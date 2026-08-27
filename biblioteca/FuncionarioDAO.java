package biblioteca;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FuncionarioDAO {

    public void salvar(Funcionario f) {
        String sql = "INSERT INTO FUNCIONARIO (nome, tl, funcao, email) VALUES (?, ?, ?, ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, f.nome);
            stmt.setString(2, f.tel);
            stmt.setString(3, f.fun);
            stmt.setString(4, f.email);

            stmt.executeUpdate();
            System.out.println("Funcionário salvo no banco com sucesso!");

        } catch (SQLException e) {
            System.out.println("Erro ao salvar no banco: " + e.getMessage());
        }
    }
}