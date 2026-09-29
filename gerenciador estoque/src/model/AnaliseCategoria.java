package model;

import java.math.BigDecimal;

public record AnaliseCategoria(
        String categoria,
        long unidadesEntrada,
        long unidadesSaida,
        BigDecimal faturamento,
        BigDecimal custoSaidas) {

    public BigDecimal lucro() {
        return faturamento.subtract(custoSaidas);
    }
}