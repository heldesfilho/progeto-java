package biblioteca;
import java.util.Scanner;
import java.util.ArrayList;
public class Biblioteca_ge {
    public static void main(String[] args) {
        ArrayList<Funcionario> biblioteca=new ArrayList<>();
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
        if (descrisao==1) {addfun(entrada, biblioteca);}
        else if(descrisao==2){}
        else if(descrisao==3){}
        else if(descrisao==4){}
        else if(descrisao==5){}
        else if(descrisao==6){break;}
        }}
    public static void addfun (Scanner entrada, ArrayList<Funcionario>biblioteca) {
        System.out.println();
        System.out.println("===============");
        System.out.println("nome:");
        String nome = entrada.nextLine();
        System.out.println("telefone ex xx xxxxx xxxx:");
        String tel =entrada.nextLine();
        Funcionario teste =new Funcionario(nome, tel);
        biblioteca.add(teste);
    }

}
