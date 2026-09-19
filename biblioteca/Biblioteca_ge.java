package biblioteca;
import java.util.Scanner;
public class Biblioteca_ge {
    public static void main(String[] args) {

        Scanner entrada =new Scanner(System.in,"UTF-8");
        while (true) {
            System.out.println("=======================");
        System.out.println("       FUNÇÕES");
        System.out.println("=======================");
        System.out.println("1- ADICIONAR FUNCIONARIO");
        System.out.println("2- REMOVER FUNCIONARIO");
        System.out.println("3- ADICIONAR LIVRO");
        System.out.println("4- ADICIONAR CLIENTE");
        System.out.println("5- EMPRESTAR LIVRO");
        System.out.println("6- SAIR");
        int descrisao=entrada.nextInt();
        entrada.nextLine();
        if (descrisao==1) {addfun(entrada);}
        else if(descrisao==2){remover(entrada);}
        else if(descrisao==3){addlivro(entrada);}
        else if(descrisao==4){addcli(entrada);}
        else if(descrisao==5){empresta(entrada);}
        else if(descrisao==6){break;}}}

    public static void addfun (Scanner entrada) {
        System.out.println();
        System.out.println("===============");
        System.out.println("Nome:");
        String nome = entrada.nextLine();
        while (nome.isBlank()) {System.out.println("Valor não pode ser vazio");
            nome = entrada.nextLine();}
        System.out.println("Telefone:");
        String tel =entrada.nextLine();
        while (tel.isBlank()) {System.out.println("Valor não pode ser vazio");
            tel = entrada.nextLine();}
        System.out.println("Função:");
        String fun =entrada.nextLine();
        while (fun.isBlank()) {System.out.println("Valor não pode ser vazio");
            fun = entrada.nextLine();}
        System.out.println("Email:");
        String email =entrada.nextLine();
        while (email.isBlank()) {System.out.println("Valor não pode ser vazio");
            email = entrada.nextLine();}
        Funcionario teste =new Funcionario(nome, tel, fun, email);
        FuncionarioDAO dao=new FuncionarioDAO();
        dao.salvar(teste);
    }
    public static void remover(Scanner entrada) {
        FuncionarioDAO dao =new FuncionarioDAO();
        System.out.println();
        System.out.println("==============");
        dao.lista();
        System.out.println("====================");
        System.out.println("Qual id do funcionario a ser deletado || 0 para sair");
        int fudelete = entrada.nextInt();
        if (fudelete==0){main(null);;}else{entrada.nextLine();
        dao.delete(fudelete);}
    }
    public static void addlivro(Scanner entrada) {
        System.out.println();
        System.out.println("==============");
        System.out.println("Codigo do livro MAX(8)");
        int codigoLivro = entrada.nextInt();
        entrada.nextLine();
        System.out.println();
        System.out.println("Nome do livro MAX(50)");
        String nomeLivro=entrada.nextLine();
        while (nomeLivro.isBlank()) {System.out.println("Valor não pode ser vazio");
            nomeLivro = entrada.nextLine();}
        System.out.println();
        System.out.println("Autor MAX(50)");
        String autorLivro=entrada.nextLine();
        while (autorLivro.isBlank()) {System.out.println("Valor não pode ser vazio");
            autorLivro = entrada.nextLine();}
        System.out.println();
        System.out.println("Quantidade MAX(3)");
        int quanLivro=entrada.nextInt();
        Livro pu = new Livro(nomeLivro, autorLivro,quanLivro, codigoLivro);
        LivroDAO lidao=new LivroDAO();
        lidao.salvar(pu);
    }
    public static void addcli(Scanner entrada) {
        System.out.println();
        System.out.println("============");
        System.out.println("Nome");
        String nomeCli=entrada.nextLine();
        while (nomeCli.isBlank()) {System.out.println("Valor não pode ser vazio");
            nomeCli = entrada.nextLine();}
        System.out.println();
        System.out.println("CPF");
        String cpf=entrada.nextLine();
        while (cpf.isBlank()) {System.out.println("Valor não pode ser vazio");
            cpf = entrada.nextLine();}
        Cliente cli=new Cliente(nomeCli, cpf);
        ClienteDAO tes=new ClienteDAO();
        tes.salvar(cli);
    }
    public static void empresta(Scanner entrada) {
        System.out.println();
        System.out.println("==============");
        System.out.println("CPF do cliente");
        String cpfEmpre=entrada.nextLine();
        ClienteDAO clienteveri =new ClienteDAO();
        while (clienteveri.cpfExiste(cpfEmpre)==false){System.out.println("CPF inválido ou não cadastrado. Digite novamente:");
    cpfEmpre = entrada.nextLine();}
        System.out.println();
        System.out.println("Codigo do livro a ser emprestado");
        int codigoEmpre=entrada.nextInt();
        entrada.nextLine();
        LivroDAO livroveri=new LivroDAO();
        while(livroveri.livroExiste(codigoEmpre)==false){
            System.out.println("Codigo não encontrado");
            codigoEmpre=entrada.nextInt();
        entrada.nextLine();}
        if (livroveri.livrotem(codigoEmpre)<=0) {
            System.out.println("Sem essa livro no estoque");return;}
        }
}
