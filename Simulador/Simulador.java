package Simulador;
import java.util.*;

public class Simulador {

/*VARIÁVEIS EDITÁVEIS
    Estas são as variáveis que podem ser editadas pelo usuário para alterar os resultados da simulação.
*/
    static double chegadaMin = 20; //Tempo mínimo de chegada na primeira fila;
    static double chegadaMax = 40; //Tempo máximo de chegada na primeira fila;

    static double primeiraChegada = 45; //Tempo da primeira chegada;

    static long a = 1675893; //Números utilizados pelo gerador de números pesudoaleatórios
    static long c = 3345554;
    static long M = 6574367296L;
    static long seed = 3;

    static int quantidadeNumeros = 100000; //Quantidade de números pseudoaleatórios a serem gerados;

    static String nomeFilaChegada =  "Fila 1"; //Nome da fila de chegada de pessoas do exterior; obs: o nome vazio "" está reservado e não pode ser usado.

    public static Fila[] filas = { //Vetor com as definições de cada fila. O construtor do objeto Fila deve receber, em ordem: nome da fila, número de funcionários, capacidade da fila, tempos mínimo e máximo de atendimento, e a lista de probabilidades de roteamento para outras filas ao sair desta;
        new Fila("Fila 1", 1, 5, 10, 12, 
            new ArrayList<>(List.of(
                new ProbabilidadeFila("Fila 2", 0.78), 
                new ProbabilidadeFila("Fila 3", 0.12)
            ))
        ),
        new Fila("Fila 2", 2, 5, 30, 120),
        new Fila("Fila 3", 2, 5, 15, 60),
    };
    
    
/*VARIÁVEIS NÃO-EDITÁVEIS
    Estas são as variáveis que são utilizadas e modificadas no decorrer do programa, não sendo modificadas pelo usuário.
 */    
    static double[] numeros = new double[quantidadeNumeros];
    //static double[] numeros = {0.7, 0.1, 0.1, 0.8, 0.1, 0.9, 0.6, 0.5, 0.1, 0.6, 0.2, 0.2, 0.7, 0.5, 0.9};
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

    public static double geraProbabilidade() {
        if (numerosUsados >= quantidadeNumeros) return -1;
        double r = numeros[numerosUsados++];
        return r;
    }

    public static String sortearFila(Fila fila) {
        if (numerosUsados >= quantidadeNumeros) return "";
        double soma = 0;
        double prob = geraProbabilidade();
        for (ProbabilidadeFila f : fila.getProbFilas()) {
            soma += f.getProbabilidade();
            if (prob < soma) {
                return f.getNomeFila();
            }
        }
        return "";
    }

    public static double geraTempo(double min, double max) {
        if (numerosUsados >= quantidadeNumeros) return 0;
        double r = numeros[numerosUsados++];
        return min + ((max - min) * r);
    }

    public static void agendarEvento(Evento e) {
        escalonador.add(e);
    }

    public static Fila getFilaPorNome(String nomeFila) {
        for (Fila f : filas) {
            if (f.getNome().equals(nomeFila)) {
                return f;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        numerosUsados = 0;
        tempoGlobal = 0;

        for(Fila f: filas) {
            f.validarNomesProbFila();
        }

        gerador();
        agendarEvento(new Evento(-1, nomeFilaChegada, primeiraChegada)); // primeira chegada
    
        while (!escalonador.isEmpty() && numerosUsados < quantidadeNumeros) {
            Evento evento = escalonador.poll();
            double tempoEvento = evento.tempoGlobal;
        
            for (Fila f : filas) {
                f.acumulaTempo(tempoEvento, tempoGlobal);
            }
        
            Fila fila = getFilaPorNome(evento.nomeFila);
            if (evento.tipo == -1) {
                fila.chegada(tempoEvento, evento.nomeFila);
            } else {
                fila.saida(evento.tipo, tempoEvento, evento.nomeFila, evento.nomeFilaDestino);
            }
        
            tempoGlobal = tempoEvento;
        }
        
    
        for (int i = 0; i < filas.length; i++) {
            filas[i].printDados(i, tempoGlobal);
        }
    
        System.out.printf("Tempo de simulaçao: %.4f%n", tempoGlobal);
    }
}
