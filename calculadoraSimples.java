import java.util.Scanner;
public class calculadoraSimples {
    public static void main(String[] args) {
        Scanner entrada =new Scanner(System.in);
        System.out.println("qual o primeiro número: ");
        double nu1 = entrada.nextDouble();
        System.out.println("qual o segundo número: ");
        double nu2 = entrada.nextDouble();
        System.out.println("qual a operação +, -, x ou / : ");
        String ope = entrada.next();
        if(ope.equals("+")){
            double soma = nu1+nu2;
            System.out.println(soma);
        } else if(ope.equals("-")){
            double ne = nu1-nu2;
            System.out.println(ne);
        }else if(ope.equals("x")){
            double mu= nu1*nu2;
            System.out.println(mu);
        } else if(ope.equals("/")){
            double di= nu1/nu2;
            System.out.println(di);
        }else{System.out.println("Operação não encontrada");}
        entrada.close();
    }
}
