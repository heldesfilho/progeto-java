package biblioteca;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LivroDAO {
    public void salvar(Livro l) {
        String sql="INSERT INTO LIVRO (codigoLivro, nomeLivro, autorLivro, quanLivro) VALUES(?,?,?,?)";
        try (Connection conn=Conexao.conectar();
    PreparedStatement sera=conn.prepareStatement(sql)){
        sera.setInt(1, l.codigoLivro);
        sera.setString(2, l.nomeLivro);
        sera.setString(3, l.autorLivro);
        sera.setInt(4, l.quanLivro);
        sera.executeUpdate();
        System.out.println("dados salvos");
            
        } catch (SQLException e) {
            System.out.println("Erro "+e.getMessage());
        }}
}
