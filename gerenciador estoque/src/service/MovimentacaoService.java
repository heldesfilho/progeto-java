package service;

import dao.MovimentacaoDAO;
import dao.ProdutoDAO;
import model.Movimentacao;
import model.Produto;
import model.TipoMovimentacao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class MovimentacaoService {

    private final MovimentacaoDAO dao = new MovimentacaoDAO();
    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    // Os métodos devolvem o novo saldo do produto.
    public int registrarEntrada(int produtoId, int quantidade, String observacao) throws SQLException {
        return registrar(produtoId, TipoMovimentacao.ENTRADA, quantidade, observacao);
    }

    public int registrarSaida(int produtoId, int quantidade, String observacao) throws SQLException {
        return registrar(produtoId, TipoMovimentacao.SAIDA, quantidade, observacao);
    }

    public List<Movimentacao> historico(int produtoId) throws SQLException {
        return dao.listarPorProduto(produtoId);
    }

    public List<Movimentacao> recentes(int limite) throws SQLException {
        return dao.listarRecentes(limite);
    }

    public List<Movimentacao> porPeriodo(LocalDate inicio, LocalDate fim) throws SQLException {
        if (inicio.isAfter(fim)) {
            throw new IllegalArgumentException("A data inicial não pode ser depois da data final.");
        }
        return dao.listarPorPeriodo(inicio, fim);
    }

    private int registrar(int produtoId, TipoMovimentacao tipo, int quantidade, String observacao)
            throws SQLException {

        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }

        Produto produto = produtoDAO.buscarPorId(produtoId);
        if (produto == null || !produto.isAtivo()) {
            throw new IllegalArgumentException("Produto não encontrado.");
        }

        // Verificação amigável; o DAO ainda garante a regra de forma atômica no banco.
        if (tipo == TipoMovimentacao.SAIDA && quantidade > produto.getQuantidadeAtual()) {
            throw new IllegalArgumentException(
                    "Saldo insuficiente. Disponível: " + produto.getQuantidadeAtual());
        }

        String obs = (observacao == null || observacao.isBlank()) ? null : observacao.trim();
        Movimentacao m = new Movimentacao(produtoId, tipo, quantidade, obs);

        if (!dao.registrar(m)) {
            throw new IllegalArgumentException(
                    "Não foi possível registrar: o saldo mudou ou o produto foi desativado. Tente novamente.");
        }

        return produtoDAO.buscarPorId(produtoId).getQuantidadeAtual();
    }
}
