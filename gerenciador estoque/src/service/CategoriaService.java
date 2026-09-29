package service;
import dao.CategoriaDAO;
import model.Categoria;
import java.sql.SQLException;
import java.util.List;
public class CategoriaService {

    private final CategoriaDAO dao = new CategoriaDAO();
    public Categoria cadastrar(String nome) throws SQLException {
        Categoria c = new Categoria(validarNome(nome, 0));
        dao.inserir(c);
        return c;}

    public void renomear(int id, String novoNome) throws SQLException {
        Categoria c = dao.buscarPorId(id);
        if (c == null) {
            throw new IllegalArgumentException("Categoria não encontrada.");}
        c.setNome(validarNome(novoNome, id));
        dao.atualizar(c);}

    public int remover(int id) throws SQLException {
        if (dao.buscarPorId(id) == null) {
            throw new IllegalArgumentException("Categoria não encontrada.");}
        int afetados = dao.contarProdutos(id);
        dao.remover(id);
        return afetados;}

    public Categoria buscarPorId(int id) throws SQLException {
        return dao.buscarPorId(id);}
    public List<Categoria> listar() throws SQLException {
        return dao.listar();}
    private String validarNome(String nome, int idAtual) throws SQLException {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome da categoria é obrigatório.");}
        String limpo = nome.trim();
        if (limpo.length() > 100) {
            throw new IllegalArgumentException("O nome pode ter no máximo 100 caracteres.");}

        Categoria existente = dao.buscarPorNome(limpo);
        if (existente != null && existente.getId() != idAtual) {
            throw new IllegalArgumentException("Já existe uma categoria com esse nome.");}
        return limpo;}
}
