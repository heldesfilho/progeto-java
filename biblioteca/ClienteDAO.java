package biblioteca;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class ClienteDAO {  
    public void salvar(Cliente c) {
        String sql="INSERT INTO CLIENTE (nomeCli, cpf) VALUES (?,?)";
         try (Connection conn=Conexao.conectar();
    PreparedStatement sera=conn.prepareStatement(sql)){
        
        sera.setString(1, c.nomeCli);
        sera.setString(2, c.cpf);
        
        sera.executeUpdate();
        System.out.println("dados salvos");
            
        } catch (SQLException e) {
            System.out.println("Erro "+e.getMessage());
        }}

    public boolean cpfExiste(String cpf) {
    String sql = "SELECT 1 FROM CLIENTE WHERE cpf = ?";
    boolean existe = false;

    try (Connection conn = Conexao.conectar();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, cpf);
        ResultSet rs = stmt.executeQuery();

        existe = rs.next();

    } catch (SQLException e) {
        System.out.println("Erro " + e.getMessage());
    }return existe;}
}
