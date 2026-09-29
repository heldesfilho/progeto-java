package model;

import java.math.BigDecimal;
public record ResumoEstoque(int produtos, long unidades, BigDecimal valorCusto, BigDecimal valorVenda) {
}
