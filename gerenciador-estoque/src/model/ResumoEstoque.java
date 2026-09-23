package model;

import java.math.BigDecimal;

// Um "record" é uma classe enxuta só para carregar dados: o Java já gera
// construtor, getters (produtos(), unidades()...) e toString automaticamente.
public record ResumoEstoque(int produtos, long unidades, BigDecimal valorCusto, BigDecimal valorVenda) {
}
