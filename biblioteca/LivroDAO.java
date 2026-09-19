package biblioteca;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

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

    public boolean livroExiste(int codigo) {
    String sql = "SELECT 1 FROM LIVRO WHERE codigoLivro = ?";
    boolean existe = false;

    try (Connection conn = Conexao.conectar();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, codigo);
        ResultSet rs = stmt.executeQuery();

        existe = rs.next();

    } catch (SQLException e) {
        System.out.println("Erro " + e.getMessage());
    }

    return existe;
}
    public int livrotem(int codigoLivro) {
    String sql = "SELECT quanLivro FROM LIVRO WHERE codigoLivro = ?";
    int quantidade = 0;

    try (Connection conn = Conexao.conectar();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, codigoLivro);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            quantidade = rs.getInt("quanLivro");
        }

    } catch (SQLException e) {
        System.out.println("Erro " + e.getMessage());
    }

    return quantidade;
}
}
