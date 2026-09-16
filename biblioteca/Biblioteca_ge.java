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
        System.out.println("6- STATOS LIVROS");
        System.out.println("7- SAIR");
        int descrisao=entrada.nextInt();
        entrada.nextLine();
        if (descrisao==1) {addfun(entrada);}
        else if(descrisao==2){remover(entrada);}
        else if(descrisao==3){addlivro(entrada);}
        else if(descrisao==4){addcli(entrada);}
        else if(descrisao==5){}
        else if(descrisao==6){}
        else if(descrisao==7){break;}}}
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
        System.out.println("Qual id do funcionario a ser deletado ");
        int fudelete = entrada.nextInt();
        while (fudelete.isBlank()) {System.out.println("Valor não pode ser vazio");
            fudelete = entrada.nextLine();}
        entrada.nextLine();
        dao.delete(fudelete);
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
        System.out.println();
        System.out.println("Autor MAX(50)");
        String autorLivro=entrada.nextLine();
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
        String nomeCli=entrada.nextLine().trim();
        System.out.println();
        System.out.println("CPF");
        String cpf=entrada.nextLine().trim();
        Cliente cli=new Cliente(nomeCli, cpf);
        ClienteDAO tes=new ClienteDAO();
        tes.salvar(cli);
    }
}
