package Simulador;
import java.util.*;

public class Simulador {

    static double chegadaMin;
    static double chegadaMax;

    static double primeiraChegada;

    static long a;
    static long c;
    static long M;
    static long seed;

    static int quantidadeNumeros;

    static String nomeFilaChegada;

    public static Fila[] filas;
 
    static double[] numeros;
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

    public static void main(String[] args) throws Exception {

        if (args.length == 0) {
            throw new IllegalArgumentException("É necessário informar o nome do arquivo .yml como argumento.");
        }
        
        String caminhoArquivo = args[0];

        if (args.length > 0) {
            caminhoArquivo = args[0];
        }
        ConfigLoader.carregarParametros(caminhoArquivo);

        // System.out.println("chegadaMin: " + chegadaMin);
        // System.out.println("chegadaMax: " + chegadaMax);
        // System.out.println();
        // System.out.println("primeiraChegada: " + primeiraChegada);
        // System.out.println();
        // System.out.println("a: " + a);
        // System.out.println("c: " + c);
        // System.out.println("M: " + M);
        // System.out.println("seed: " + seed);
        // System.out.println();
        // System.out.println("quantidadeNumeros: " + quantidadeNumeros);
        // System.out.println();
        // System.out.println("nomeFilaChegada: " + nomeFilaChegada);
        // System.out.println();

        // for (Fila f: filas) {
        //     System.out.println("   fila" + f.getNome() + ": funcionarios: " + f.getNumFuncionarios() + ", capacidade: " + f.getCapacidade() + ", minServico: " + f.getMinServico() + ", maxServico: " + f.getMaxServico());
        //     for (ProbabilidadeFila pf: f.getProbFilas()) {
        //         System.out.println("      Prob fila " + pf.getNomeFila() + ": " + pf.getProbabilidade());
        //     }
        // }

        numerosUsados = 0;
        tempoGlobal = 0;
        numeros = new double[quantidadeNumeros];

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
