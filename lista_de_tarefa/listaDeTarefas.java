import java.util.Scanner;
import java.util.ArrayList;
public class listaDeTarefas {
    public static void main(String[] args) {
        Scanner entrada = new Scanner(System.in);
        ArrayList<Tarefa> lista=new ArrayList<>();
        while (true){
            System.out.println("");
            System.out.println("O que deseja");
            System.out.println("1-Ver lista");
            System.out.println("2-Adicionar tarefa");
            System.out.println("3-remover tarefa");
            System.out.println("4-Tarefa feita");
            System.out.println("5-Tarefa não feita");
            System.out.println("6-sair");
            int fun=entrada.nextInt();
        
            if (fun==1){
                list(lista);
            }
            else if(fun==2){
                adi(entrada,lista);
            }
            else if(fun==3){
                remo(entrada, lista);
            }
            else if(fun==4){
                feita(entrada,lista);
            }
            else if(fun==5){
                notfeito(entrada,lista);
            }
            else if(fun==6){break;}
        }entrada.close();
    }
    public static void list(ArrayList<Tarefa> lista) {
        System.out.println("========TAREFAS=======");
        for ( Tarefa tarefa : lista){
            if(tarefa.feito==true){
            System.out.println(tarefa.nome+" - Feito");
        }else{
            System.out.println(tarefa.nome+" - Não feito");
        }
        }
    }
    public static void adi(Scanner entrada, ArrayList<Tarefa>lista) {
        System.out.println("O que vai ser adicionado: ");
        entrada.nextLine();
        String nome =entrada.nextLine();
        Tarefa tarefa =new Tarefa();
        tarefa.nome=nome;
        lista.add(tarefa);
    }
    public static void remo(Scanner entrada, ArrayList<Tarefa>lista) {
        if(lista.size()>0){
            for(int i=0; i < lista.size(); i++){
            System.out.println((i+1)+" - "+lista.get(i).nome);
        }
        System.out.println("Escolha o número para ser removido");
        int re = entrada.nextInt();
        int fi = re-1;
        if(fi >= 0 && fi<lista.size()){
            lista.remove(fi);
        }else{System.out.println("número não esistente");}}else{System.out.println("Lista vazia");}       
    }
    public static void feita(Scanner entrada,ArrayList<Tarefa> lista) {
        if(lista.size()>0){
            for(int i=0; i<lista.size(); i++){
                System.out.println((i+1)+" - "+lista.get(i).nome);}
        System.out.println("");
        System.out.println("Escolha o número que foi feito");
        int re=entrada.nextInt();
        int fi=re-1;
        if (fi>= 0 && fi<lista.size()) {
            lista.get(fi).feito=true;}}else{System.out.println("Lista vazia");}
    }
    public static void notfeito (Scanner entrada, ArrayList<Tarefa>lista) {
        if(lista.size()>0){
            for(int i=0; i<lista.size(); i++){
                System.out.println((i+1)+" - "+lista.get(i).nome);}
        System.out.println("");
        System.out.println("Escolha o número que não foi feito");
        int re=entrada.nextInt();
        int fi=re-1;
        if (fi>=0&&fi<lista.size()){
            lista.get(fi).feito=false;}}else{System.out.println("Lista vazia");}
    }
}