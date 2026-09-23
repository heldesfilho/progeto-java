package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBD {

    private static final String URL =
            "jdbc:mysql://localhost:3306/estoque_loja?useSSL=false&serverTimezone=America/Sao_Paulo";
    private static final String USUARIO = "root";   // troque pelo seu usuário
    private static final String SENHA = "SOMBRA-mf10";         // troque pela sua senha

    // Cada chamada devolve uma conexão nova; quem usa deve fechá-la (try-with-resources).
    public static Connection getConexao() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}
