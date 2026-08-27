package biblioteca;
import java.util.Scanner;
public class Biblioteca_ge {
    public static void main(String[] args) {
        Scanner entrada =new Scanner(System.in);
        while (true) {
            System.out.println("=======================");
        System.out.println("       FUNÇÕES");
        System.out.println("=======================");
        System.out.println("1- ADICIONAR FUNCIONARIO");
        System.out.println("2- REMOVER FUNCIONARIO");
        System.out.println("3- ADICIONAR LIVRO");
        System.out.println("4- CLIENTE LIVRO");
        System.out.println("5- STATOS LIVROS");
        System.out.println("6- SAIR");
        int descrisao=entrada.nextInt();
        entrada.nextLine();
        if (descrisao==1) {addfun(entrada);}
        else if(descrisao==2){}
        else if(descrisao==3){}
        else if(descrisao==4){}
        else if(descrisao==5){}
        else if(descrisao==6){break;}
        }}
    public static void addfun (Scanner entrada) {
        System.out.println();
        System.out.println("===============");
        System.out.println("Nome:");
        String nome = entrada.nextLine();
        System.out.println("Telefone:");
        String tel =entrada.nextLine();
        System.out.println("Função:");
        String fun =entrada.nextLine();
        System.out.println("Email:");
        String email =entrada.nextLine();
        Funcionario teste =new Funcionario(nome, tel, fun, email);
        FuncionarioDAO dao=new FuncionarioDAO();
        dao.salvar(teste);
    }
    public static void remover(Scanner entrada) {
        
    }
}
