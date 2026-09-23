package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Movimentacao {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private int id;
    private int produtoId;
    private String produtoNome;          // preenchido só nas consultas (JOIN)
    private TipoMovimentacao tipo;
    private int quantidade;
    private LocalDateTime dataHora;      // definida pelo banco ao inserir
    private String observacao;

    public Movimentacao() {
    }

    // Construtor para uma movimentação nova
    public Movimentacao(int produtoId, TipoMovimentacao tipo, int quantidade, String observacao) {
        this.produtoId = produtoId;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.observacao = observacao;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProdutoId() { return produtoId; }
    public void setProdutoId(int produtoId) { this.produtoId = produtoId; }

    public String getProdutoNome() { return produtoNome; }
    public void setProdutoNome(String produtoNome) { this.produtoNome = produtoNome; }

    public TipoMovimentacao getTipo() { return tipo; }
    public void setTipo(TipoMovimentacao tipo) { this.tipo = tipo; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    @Override
    public String toString() {
        String data = (dataHora == null) ? "-" : dataHora.format(FORMATO);
        String sinal = (tipo == TipoMovimentacao.ENTRADA) ? "+" : "-";
        String obs = (observacao == null) ? "" : " | " + observacao;
        return String.format("%s | %-7s | %s%d | %s%s", data, tipo, sinal, quantidade, produtoNome, obs);
    }
}
