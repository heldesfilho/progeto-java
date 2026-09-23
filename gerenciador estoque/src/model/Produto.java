package model;

import java.math.BigDecimal;

public class Produto {

    private int id;
    private String codigo;
    private String nome;
    private Integer categoriaId;              // pode ser null (sem categoria)
    private String categoriaNome;             // preenchido só nas consultas (JOIN)
    private BigDecimal precoCusto = BigDecimal.ZERO;
    private BigDecimal precoVenda = BigDecimal.ZERO;
    private int quantidadeAtual;              // só muda via movimentação (próxima etapa)
    private int estoqueMinimo;
    private boolean ativo = true;

    public Produto() {
    }

    // Construtor para um produto novo (ainda sem id e com saldo zero)
    public Produto(String codigo, String nome, Integer categoriaId,
                   BigDecimal precoCusto, BigDecimal precoVenda, int estoqueMinimo) {
        this.codigo = codigo;
        this.nome = nome;
        this.categoriaId = categoriaId;
        this.precoCusto = precoCusto;
        this.precoVenda = precoVenda;
        this.estoqueMinimo = estoqueMinimo;
    }

    public boolean isEstoqueBaixo() {
        return quantidadeAtual <= estoqueMinimo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Integer getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Integer categoriaId) { this.categoriaId = categoriaId; }

    public String getCategoriaNome() { return categoriaNome; }
    public void setCategoriaNome(String categoriaNome) { this.categoriaNome = categoriaNome; }

    public BigDecimal getPrecoCusto() { return precoCusto; }
    public void setPrecoCusto(BigDecimal precoCusto) { this.precoCusto = precoCusto; }

    public BigDecimal getPrecoVenda() { return precoVenda; }
    public void setPrecoVenda(BigDecimal precoVenda) { this.precoVenda = precoVenda; }

    public int getQuantidadeAtual() { return quantidadeAtual; }
    public void setQuantidadeAtual(int quantidadeAtual) { this.quantidadeAtual = quantidadeAtual; }

    public int getEstoqueMinimo() { return estoqueMinimo; }
    public void setEstoqueMinimo(int estoqueMinimo) { this.estoqueMinimo = estoqueMinimo; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    @Override
    public String toString() {
        String prefixo = (codigo == null) ? "" : codigo + " - ";
        String categoria = (categoriaNome == null) ? "sem categoria" : categoriaNome;
        return String.format("[%d] %s%s | %s | custo R$ %.2f | venda R$ %.2f | saldo %d (mín. %d)%s",
                id, prefixo, nome, categoria, precoCusto, precoVenda, quantidadeAtual, estoqueMinimo,
                isEstoqueBaixo() ? "  <-- ESTOQUE BAIXO" : "");
    }
}
