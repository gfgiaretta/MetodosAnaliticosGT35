package Simulador;
import java.util.*;

public class Simulador {

/*VARIÁVEIS EDITÁVEIS
    Estas são as variáveis que podem ser editadas pelo usuário para alterar os resultados da simulação.
*/
    static double chegadaMin = 1; //Tempo mínimo de chegada na primeira fila;
    static double chegadaMax = 4; //Tempo máximo de chegada na primeira fila;

    static double primeiraChegada = 1.5; //Tempo da primeira chegada;

    static long a = 1675893; //Números utilizados pelo gerador de números pesudoaleatórios
    static long c = 3345554;
    static long M = 6574367296L;
    static long seed = 3;

    static int quantidadeNumeros = 100000; //Quantidade de números pseudoaleatórios a serem gerados;

    public static Fila[] filas = { //Vetor com as definições de cada fila. O construtor do objeto Fila deve receber, em ordem: número de funcionários, capacidade da fila, tempo mínimo de atendimento e tempo máximo de atendimento;
        new Fila(2, 3, 3, 4),
        new Fila(1, 5, 2, 3)
    };
    
/*VARIÁVEIS NÃO-EDITÁVEIS
    Estas são as variáveis que são utilizadas e modificadas no decorrer do programa, não sendo modificadas pelo usuário.
 */    
    static double[] numeros = new double[quantidadeNumeros];
    static int numerosUsados;
    static double tempoGlobal;

    private static PriorityQueue<Evento> escalonador = new PriorityQueue<>();

    public static double[] gerador() {
        long s = seed;
        for (int i = 0; i < quantidadeNumeros; i++) {
            s = (a * s + c) % M;
            numeros[i] = (double) s / M;
        }
        return numeros;
    }

    public static double geraTempo(double min, double max) {
        if (numerosUsados >= quantidadeNumeros) return 0;
        double r = numeros[numerosUsados++];
        return min + ((max - min) * r);
    }

    public static void agendarEvento(Evento e) {
        escalonador.add(e);
    }

    public static void main(String[] args) {
        numerosUsados = 0;
        tempoGlobal = 0;

        gerador();
        agendarEvento(new Evento(-1, 0, primeiraChegada)); // primeira chegada
    
        while (!escalonador.isEmpty() && numerosUsados < quantidadeNumeros) {
            Evento evento = escalonador.poll();
            double tempoEvento = evento.tempoGlobal;
        
            for (Fila f : filas) {
                f.acumulaTempo(tempoEvento, tempoGlobal);
            }
        
            Fila fila = filas[evento.fila];
            if (evento.tipo == -1) {
                fila.chegada(tempoEvento, evento.fila);
            } else {
                fila.saida(evento.tipo, tempoEvento, evento.fila);
            }
        
            tempoGlobal = tempoEvento;
        }
        
    
        for (int i = 0; i < filas.length; i++) {
            filas[i].printDados(i, tempoGlobal);
        }
    
        System.out.printf("Tempo de simulaçao: %.4f%n", tempoGlobal);
    }
}
