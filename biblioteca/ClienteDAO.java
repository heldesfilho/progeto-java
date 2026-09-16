package biblioteca;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
}
