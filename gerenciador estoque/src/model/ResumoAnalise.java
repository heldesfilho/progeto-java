package model;

import java.math.BigDecimal;

public record ResumoAnalise(
        long totalEntradas,
        long totalSaidas,
        BigDecimal faturamento,
        BigDecimal custoSaidas,
        BigDecimal custoEntradas) {

    public BigDecimal lucro() {
        return faturamento.subtract(custoSaidas);
    }
}