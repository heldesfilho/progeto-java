import model.AnaliseCategoria;
import model.Cargo;
import model.Categoria;
import model.ItemVenda;
import model.Produto;
import model.Movimentacao;
import model.ResultadoVenda;
import model.ResumoAnalise;
import model.ResumoEstoque;
import model.Usuario;
import service.AnaliseService;
import service.CategoriaService;
import service.MovimentacaoService;
import service.ProdutoService;
import service.UsuarioService;
import service.VendaService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MainTeste {

    private static final Scanner sc = new Scanner(System.in);
    private static final ProdutoService produtoService = new ProdutoService();
    private static final CategoriaService categoriaService = new CategoriaService();
    private static final MovimentacaoService movimentacaoService = new MovimentacaoService();
    private static final UsuarioService usuarioService = new UsuarioService();
    private static final VendaService vendaService = new VendaService();
    private static final AnaliseService analiseService = new AnaliseService();

    public static void main(String[] args) throws SQLException {
        Usuario logado = telaDeAcesso();
        System.out.println("\nBem-vindo, " + logado.getUsuario() + "!\n");

        int opcao;
        do {
            imprimirMenu(logado);
            opcao = lerInt("Opção: ");
            try {
                executar(opcao, logado);
            } catch (IllegalArgumentException e) {
                System.out.println("[Erro de regra] " + e.getMessage());
            } catch (SQLException e) {
                System.out.println("[Erro de banco] " + e.getMessage());
            } catch (Exception e) {
                System.out.println("[Erro inesperado] " + e);
            }
            System.out.println();
        } while (opcao != 0);}
    private static Usuario telaDeAcesso() throws SQLException {
        while (true) {
            String usuario = lerString("Usuário: ");
            String senha = lerString("Senha: ");
            Usuario logado = usuarioService.autenticar(usuario, senha);
            if (logado != null) {
                return logado;
            }
            System.out.println("Usuário ou senha inválidos.\n");}}
    private static void imprimirMenu(Usuario logado) {
        System.out.println("""
                === PRODUTOS ===
                1  - Cadastrar produto
                2  - Listar produtos ativos
                3  - Buscar produto por nome
                4  - Buscar produto por código
                5  - Atualizar produto
                6  - Desativar produto
                7  - Listar estoque baixo
                8  - Listar por categoria
                9  - Resumo do estoque
                === CATEGORIAS ===
                10 - Cadastrar categoria
                11 - Renomear categoria
                12 - Remover categoria
                13 - Listar categorias
                === MOVIMENTAÇÕES ===
                14 - Vender 
                15 - Registrar entrada
                16 - Registrar saída avulsa
                17 - Histórico por produto
                18 - Movimentações recentes
                19 - Movimentações por período""");
        if (logado.getCargo() == Cargo.GERENTE) {
            System.out.println("""
                    === ADMINISTRAÇÃO (somente gerente) ===
                    20 - Criar funcionário
                    21 - Análise geral (faturamento, lucro/prejuízo, por categoria)""");}
        System.out.println("0  - Sair");}

    private static void executar(int opcao, Usuario logado) throws SQLException {
        switch (opcao) {
            case 1 -> cadastrarProduto();
            case 2 -> produtoService.listar().forEach(System.out::println);
            case 3 -> {
                String termo = lerString("Nome (ou parte): ");
                produtoService.buscarPorNome(termo).forEach(System.out::println);}
            case 4 -> {
                String codigo = lerString("Código: ");
                Produto p = produtoService.buscarPorCodigo(codigo);
                System.out.println(p == null ? "Não encontrado." : p);}
            case 5 -> atualizarProduto();
            case 6 -> {
                int id = lerInt("ID do produto: ");
                produtoService.desativar(id);
                System.out.println("Desativado.");}
            case 7 -> produtoService.listarEstoqueBaixo().forEach(System.out::println);
            case 8 -> {
                String catId = lerString("ID da categoria (vazio = sem categoria): ");
                Integer id = catId.isBlank() ? null : Integer.valueOf(catId);
                produtoService.listarPorCategoria(id).forEach(System.out::println);}
            case 9 -> {
                ResumoEstoque r = produtoService.resumoEstoque();
                System.out.printf("Produtos: %d | Unidades: %d | Custo total: R$ %.2f | Venda total: R$ %.2f%n",
                        r.produtos(), r.unidades(), r.valorCusto(), r.valorVenda());}
            case 10 -> {
                String nome = lerString("Nome da categoria: ");
                System.out.println("Criada: " + categoriaService.cadastrar(nome));}
            case 11 -> {
                int id = lerInt("ID da categoria: ");
                String novoNome = lerString("Novo nome: ");
                categoriaService.renomear(id, novoNome);
                System.out.println("Renomeada.");}
            case 12 -> {
                int id = lerInt("ID da categoria: ");
                int afetados = categoriaService.remover(id);
                System.out.println("Removida. Produtos afetados (ficaram sem categoria): " + afetados);}
            case 13 -> categoriaService.listar().forEach(System.out::println);
            case 14 -> vender();
            case 15 -> registrarMovimentacao(true);
            case 16 -> registrarMovimentacao(false);
            case 17 -> {
                int id = lerInt("ID do produto: ");
                movimentacaoService.historico(id).forEach(System.out::println);}
            case 18 -> {
                int limite = lerInt("Quantas? ");
                movimentacaoService.recentes(limite).forEach(System.out::println);}
            case 19 -> {
                LocalDate inicio = lerData("Data inicial (yyyy-MM-dd): ");
                LocalDate fim = lerData("Data final (yyyy-MM-dd): ");
                List<Movimentacao> lista = movimentacaoService.porPeriodo(inicio, fim);
                lista.forEach(System.out::println);
            }
            case 20 -> criarFuncionario(logado);
            case 21 -> analise(logado);
            case 0 -> System.out.println("Saindo...");
            default -> System.out.println("Opção inválida.");}}
    // parte da venda

    private static void vender() throws SQLException {
        Map<Integer, Integer> quantidades = new LinkedHashMap<>(); 
        Map<Integer, Produto> produtos = new LinkedHashMap<>();    

        while (true) {
            System.out.println("\n--- VENDA --- (itens no carrinho: " + quantidades.size() + ")");
            System.out.println("1 - Listar produtos disponíveis (com estoque)");
            System.out.println("2 - Buscar produto por nome");
            System.out.println("3 - Adicionar item ao carrinho");
            System.out.println("4 - Finalizar venda");
            System.out.println("0 - Cancelar venda");
            int opcao = lerInt("Opção: ");

            switch (opcao) {
                case 1 -> produtoService.listar().stream()
                        .filter(p -> p.getQuantidadeAtual() > 0)
                        .forEach(System.out::println);
                case 2 -> {
                    String termo = lerString("Nome (ou parte): ");
                    produtoService.buscarPorNome(termo).stream()
                            .filter(p -> p.getQuantidadeAtual() > 0)
                            .forEach(System.out::println);
                }
                case 3 -> adicionarAoCarrinho(quantidades, produtos);
                case 4 -> {
                    if (quantidades.isEmpty()) {
                        System.out.println("Carrinho vazio.");
                        break;}
                    finalizarVenda(quantidades, produtos);
                    return;}
                case 0 -> {
                    System.out.println("Venda cancelada.");
                    return;}
                default -> System.out.println("Opção inválida.");}}}

    private static void adicionarAoCarrinho(Map<Integer, Integer> quantidades, Map<Integer, Produto> produtos)
            throws SQLException {
        int id = lerInt("ID do produto: ");
        Produto p = produtoService.buscarPorId(id);
        if (p == null) {
            System.out.println("Produto não encontrado ou inativo.");
            return;}
        int qtd = lerInt("Quantidade: ");
        if (qtd <= 0) {
            System.out.println("Quantidade deve ser maior que zero.");
            return;}
        int jaNoCarrinho = quantidades.getOrDefault(id, 0);
        if (jaNoCarrinho + qtd > p.getQuantidadeAtual()) {
            System.out.println("Estoque insuficiente. Disponível: " + p.getQuantidadeAtual()
                    + " (já tem " + jaNoCarrinho + " no carrinho).");
            return;}
        quantidades.merge(id, qtd, Integer::sum);
        produtos.put(id, p);
        System.out.println("Adicionado: " + qtd + "x " + p.getNome());}

    private static void finalizarVenda(Map<Integer, Integer> quantidades, Map<Integer, Produto> produtos)
            throws SQLException {

        BigDecimal total = BigDecimal.ZERO;
        System.out.println("\n--- RESUMO DA VENDA ---");
        for (var entrada : quantidades.entrySet()) {
            Produto p = produtos.get(entrada.getKey());
            int qtd = entrada.getValue();
            BigDecimal subtotal = p.getPrecoVenda().multiply(BigDecimal.valueOf(qtd));
            total = total.add(subtotal);
            System.out.printf("%dx %s - R$ %.2f%n", qtd, p.getNome(), subtotal);}
        System.out.printf("TOTAL: R$ %.2f%n", total);

        String confirmar = lerString("Confirmar venda? (s/n): ");
        if (!confirmar.equalsIgnoreCase("s")) {
            System.out.println("Venda cancelada.");
            return;}

        List<ItemVenda> itens = new ArrayList<>();
        for (var entrada : quantidades.entrySet()) {
            itens.add(new ItemVenda(entrada.getKey(), entrada.getValue()));}

        List<ResultadoVenda> resultados = vendaService.vender(itens);
        System.out.println("Venda concluída. Baixas registradas:");
        for (ResultadoVenda r : resultados) {
            System.out.println("  " + r.produto().getNome() + ": -" + r.quantidade()
                    + " (novo saldo: " + r.novoSaldo() + ")");}}

    // somente gerente
    private static void criarFuncionario(Usuario logado) throws SQLException {
        String usuario = lerString("Usuário do novo funcionário: ");
        String senha = lerString("Senha (mín. 4 caracteres): ");

        System.out.println("Cargo: 1 - Subgerente | 2 - Funcionário");
        int opcaoCargo = lerInt("Opção: ");
        Cargo cargo = switch (opcaoCargo) {
            case 1 -> Cargo.SUBGERENTE;
            case 2 -> Cargo.FUNCIONARIO;
            default -> throw new IllegalArgumentException("Cargo inválido.");};

        usuarioService.criarFuncionario(logado, usuario, senha, cargo);
        System.out.println("Funcionário criado: " + usuario + " (" + cargo + ")");}

    private static void analise(Usuario logado) throws SQLException {
        System.out.println("Período: 1 - Tudo | 2 - Escolher datas");
        int opcaoPeriodo = lerInt("Opção: ");

        LocalDate inicio;
        LocalDate fim;
        if (opcaoPeriodo == 2) {
            inicio = lerData("Data inicial (yyyy-MM-dd): ");
            fim = lerData("Data final (yyyy-MM-dd): ");
        } else {
            inicio = LocalDate.of(2000, 1, 1);
            fim = LocalDate.now();}

        ResumoAnalise r = analiseService.resumoGeral(logado, inicio, fim);
        System.out.println("\n=== ANÁLISE GERAL (" + inicio + " a " + fim + ") ===");
        System.out.println("Unidades que entraram: " + r.totalEntradas());
        System.out.println("Unidades que saíram: " + r.totalSaidas());
        System.out.printf("Faturamento (vendas ao preço atual): R$ %.2f%n", r.faturamento());
        System.out.printf("Custo das saídas: R$ %.2f%n", r.custoSaidas());
        System.out.printf("Custo investido em entradas (reposição): R$ %.2f%n", r.custoEntradas());
        BigDecimal resultado = r.lucro();
        System.out.printf("Resultado (faturamento - custo das saídas): R$ %.2f (%s)%n",
                resultado, resultado.signum() >= 0 ? "lucro" : "prejuízo");
        System.out.println("\n--- POR CATEGORIA ---");
        List<AnaliseCategoria> porCategoria = analiseService.porCategoria(logado, inicio, fim);
        if (porCategoria.isEmpty()) {
            System.out.println("Sem movimentações no período.");}
        for (AnaliseCategoria c : porCategoria) {
            BigDecimal resultadoCategoria = c.lucro();
            System.out.printf("%s | entrou: %d | saiu: %d | faturamento: R$ %.2f | resultado: R$ %.2f (%s)%n",
                    c.categoria(), c.unidadesEntrada(), c.unidadesSaida(), c.faturamento(),
                    resultadoCategoria, resultadoCategoria.signum() >= 0 ? "lucro" : "prejuízo");}}

    private static void cadastrarProduto() throws SQLException {
        String codigo = lerString("Código (opcional, enter p/ pular): ");
        String nome = lerString("Nome: ");
        String catId = lerString("ID da categoria (vazio = sem categoria): ");
        Integer categoriaId = catId.isBlank() ? null : Integer.valueOf(catId);
        BigDecimal custo = new BigDecimal(lerString("Preço de custo: "));
        BigDecimal venda = new BigDecimal(lerString("Preço de venda: "));
        int estoqueMinimo = lerInt("Estoque mínimo: ");

        Produto p = new Produto(codigo.isBlank() ? null : codigo, nome, categoriaId, custo, venda, estoqueMinimo);
        produtoService.cadastrar(p);
        System.out.println("Cadastrado: " + p);}

    private static void atualizarProduto() throws SQLException {
        int id = lerInt("ID do produto a atualizar: ");
        Produto atual = produtoService.buscarPorId(id);
        if (atual == null) {
            System.out.println("Produto não encontrado ou inativo.");
            return;}

        System.out.println("Atual: " + atual);
        atual.setNome(lerString("Novo nome [" + atual.getNome() + "]: ", atual.getNome()));
        atual.setPrecoCusto(new BigDecimal(lerString("Novo custo [" + atual.getPrecoCusto() + "]: ", atual.getPrecoCusto().toString())));
        atual.setPrecoVenda(new BigDecimal(lerString("Nova venda [" + atual.getPrecoVenda() + "]: ", atual.getPrecoVenda().toString())));
        atual.setEstoqueMinimo(Integer.parseInt(lerString("Novo mínimo [" + atual.getEstoqueMinimo() + "]: ", String.valueOf(atual.getEstoqueMinimo()))));
        produtoService.atualizar(atual);
        System.out.println("Atualizado.");}

    private static void registrarMovimentacao(boolean entrada) throws SQLException {
        int produtoId = lerInt("ID do produto: ");
        int quantidade = lerInt("Quantidade: ");
        String obs = lerString("Observação (opcional): ");
        int saldo = entrada
                ? movimentacaoService.registrarEntrada(produtoId, quantidade, obs)
                : movimentacaoService.registrarSaida(produtoId, quantidade, obs);
        System.out.println("OK. Novo saldo: " + saldo);}

    private static int lerInt(String prompt) {
        System.out.print(prompt);
        return Integer.parseInt(sc.nextLine().trim());}

    private static String lerString(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();}

    private static String lerString(String prompt, String padrao) {
        System.out.print(prompt);
        String v = sc.nextLine().trim();
        return v.isBlank() ? padrao : v;}

    private static LocalDate lerData(String prompt) {
        System.out.print(prompt);
        return LocalDate.parse(sc.nextLine().trim());}
    }