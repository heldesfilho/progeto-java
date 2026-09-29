package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBD {

    public static Connection getConexao() throws SQLException {
        return DriverManager.getConnection(
                exigirVariavel("DB_URL"),
                exigirVariavel("DB_USER"),
                exigirVariavel("DB_PASSWORD"));
    }

    private static String exigirVariavel(String nome) throws SQLException {
        String valor = System.getenv(nome);
        if (valor == null || valor.isBlank()) {
            throw new SQLException("Configure a variável de ambiente " + nome + " antes de iniciar.");
        }
        return valor;
    }
}
