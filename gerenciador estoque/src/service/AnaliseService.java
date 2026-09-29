package service;

import dao.MovimentacaoDAO;
import model.AnaliseCategoria;
import model.Cargo;
import model.ResumoAnalise;
import model.Usuario;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class AnaliseService {

    private final MovimentacaoDAO dao = new MovimentacaoDAO();

    public ResumoAnalise resumoGeral(Usuario solicitante, LocalDate inicio, LocalDate fim) throws SQLException {
        exigirGerente(solicitante);
        validarPeriodo(inicio, fim);
        return dao.resumoGeral(inicio, fim);}

    public List<AnaliseCategoria> porCategoria(Usuario solicitante, LocalDate inicio, LocalDate fim) throws SQLException {
        exigirGerente(solicitante);
        validarPeriodo(inicio, fim);
        return dao.analisePorCategoria(inicio, fim);}
    private void exigirGerente(Usuario solicitante) {
        if (solicitante == null || solicitante.getCargo() != Cargo.GERENTE) {
            throw new IllegalArgumentException("Apenas o gerente pode acessar a análise.");}}
    private void validarPeriodo(LocalDate inicio, LocalDate fim) {
        if (inicio.isAfter(fim)) {
            throw new IllegalArgumentException("A data inicial não pode ser depois da data final.");}}
}