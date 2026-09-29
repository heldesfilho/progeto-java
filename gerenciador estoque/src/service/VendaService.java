package service;

import dao.ProdutoDAO;
import model.ItemVenda;
import model.Produto;
import model.ResultadoVenda;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VendaService {

    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private final MovimentacaoService movimentacaoService = new MovimentacaoService();

    public List<ResultadoVenda> vender(List<ItemVenda> itens) throws SQLException {
        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("O carrinho está vazio.");}

        for (ItemVenda item : itens) {
            if (item.quantidade() <= 0) {
                throw new IllegalArgumentException("Quantidade inválida para o produto " + item.produtoId() + ".");}
            Produto p = produtoDAO.buscarPorId(item.produtoId());
            if (p == null || !p.isAtivo()) {
                throw new IllegalArgumentException("Produto " + item.produtoId() + " não encontrado.");}
            if (item.quantidade() > p.getQuantidadeAtual()) {
                throw new IllegalArgumentException(
                        "Estoque insuficiente para " + p.getNome() + ". Disponível: " + p.getQuantidadeAtual());}}

        List<ResultadoVenda> resultados = new ArrayList<>();
        for (ItemVenda item : itens) {
            Produto p = produtoDAO.buscarPorId(item.produtoId());
            int novoSaldo = movimentacaoService.registrarSaida(item.produtoId(), item.quantidade(), "Venda");
            resultados.add(new ResultadoVenda(p, item.quantidade(), novoSaldo));
        }
        return resultados;}
}