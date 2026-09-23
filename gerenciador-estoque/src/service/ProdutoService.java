package service;

import dao.CategoriaDAO;
import dao.ProdutoDAO;
import model.Produto;
import model.ResumoEstoque;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ProdutoService {

    private final ProdutoDAO dao = new ProdutoDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    public Produto cadastrar(Produto p) throws SQLException {
        validar(p);
        dao.inserir(p);
        return p;
    }

    public void atualizar(Produto p) throws SQLException {
        if (dao.buscarPorId(p.getId()) == null) {
            throw new IllegalArgumentException("Produto não encontrado.");
        }
        validar(p);
        dao.atualizar(p);
    }

    public void desativar(int id) throws SQLException {
        if (!dao.desativar(id)) {
            throw new IllegalArgumentException("Produto não encontrado.");
        }
    }

    // Devolve o produto apenas se existir e estiver ativo; senão, null.
    public Produto buscarPorId(int id) throws SQLException {
        Produto p = dao.buscarPorId(id);
        return (p != null && p.isAtivo()) ? p : null;
    }

    public List<Produto> listar() throws SQLException {
        return dao.listarAtivos();
    }

    public List<Produto> buscarPorNome(String termo) throws SQLException {
        return dao.buscarPorNome(termo.trim());
    }

    // Devolve o produto ativo com esse código, ou null se não houver.
    public Produto buscarPorCodigo(String codigo) throws SQLException {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("Informe um código.");
        }
        Produto p = dao.buscarPorCodigo(codigo.trim());
        return (p != null && p.isAtivo()) ? p : null;
    }

    public List<Produto> listarEstoqueBaixo() throws SQLException {
        return dao.listarEstoqueBaixo();
    }

    // categoriaId null = produtos sem categoria.
    public List<Produto> listarPorCategoria(Integer categoriaId) throws SQLException {
        if (categoriaId != null && categoriaDAO.buscarPorId(categoriaId) == null) {
            throw new IllegalArgumentException("Categoria não encontrada.");
        }
        return dao.listarPorCategoria(categoriaId);
    }

    public ResumoEstoque resumoEstoque() throws SQLException {
        return dao.resumoEstoque();
    }

    // Regras de negócio: lançam IllegalArgumentException com mensagem amigável.
    private void validar(Produto p) throws SQLException {
        if (p.getNome() == null || p.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome é obrigatório.");
        }
        if (p.getPrecoCusto().compareTo(BigDecimal.ZERO) < 0
                || p.getPrecoVenda().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Os preços não podem ser negativos.");
        }
        if (p.getEstoqueMinimo() < 0) {
            throw new IllegalArgumentException("O estoque mínimo não pode ser negativo.");
        }
        if (p.getCategoriaId() != null && categoriaDAO.buscarPorId(p.getCategoriaId()) == null) {
            throw new IllegalArgumentException("Categoria não encontrada.");
        }

        p.setNome(p.getNome().trim());

        // O código é opcional: vazio vira null (o MySQL permite vários NULL em coluna UNIQUE).
        if (p.getCodigo() != null) {
            String codigo = p.getCodigo().trim();
            p.setCodigo(codigo.isEmpty() ? null : codigo);
        }

        if (p.getCodigo() != null) {
            Produto existente = dao.buscarPorCodigo(p.getCodigo());
            if (existente != null && existente.getId() != p.getId()) {
                throw new IllegalArgumentException("Já existe um produto com esse código (mesmo que desativado).");
            }
        }
    }
}
