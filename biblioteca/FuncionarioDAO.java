package biblioteca;
import java.sql.ResultSet;
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
    public void delete(int id) {
        String sql = "DELETE FROM FUNCIONARIO WHERE id = ?";
        try(Connection conn = Conexao.conectar();
        PreparedStatement stmt =conn.prepareStatement(sql)){
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Funcionario deletado!");
        }catch(SQLException e){
            System.out.println("Erro "+e.getMessage());
        }
    }
    public void lista() {
        String sql="SELECT * FROM FUNCIONARIO";
        try(Connection conn = Conexao.conectar();
            PreparedStatement stmt=conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){
                System.out.println("======================");
                System.out.println("Lista dos funcionarios");
                System.out.println("======================");

            while (rs.next()) {
                int id=rs.getInt("id");
                String nome =rs.getString("nome");
                String tel=rs.getString("tl");
                String fun=rs.getString("funcao");
                String email=rs.getString("email");
            System.out.println("id-"+id+" nome-"+nome+" telefone-"+tel+" função-"+fun+" email-"+email);
        }
        } catch (SQLException e) {
            System.out.println("Erro "+e.getMessage());
        }
    }
}