package Simulador;
import java.util.Arrays;

public class Fila {
    private int numFuncionarios;
    private int capFila;
    private double atendimentoMin;
    private double atendimentoMax;

    private int pessoas = 0;
    private int perdaClientes = 0;
    private double[] tempos;
    private double[] funcionarios;

    public Fila(int numFuncionarios, int capFila, double atendimentoMin, double atendimentoMax) {
        this.numFuncionarios = numFuncionarios;
        this.capFila = capFila;
        this.atendimentoMin = atendimentoMin;
        this.atendimentoMax = atendimentoMax;

        this.tempos = new double[capFila + 1];
        this.funcionarios = new double[numFuncionarios];
        Arrays.fill(this.funcionarios, -1);
    }

    public double[] getTempos() {
        return tempos;
    }

    public int getPerdaClientes() {
        return perdaClientes;
    }

    public int buscaFuncionarioLivre() {
        for (int i = 0; i < numFuncionarios; i++) {
            if (funcionarios[i] < 0) return i;
        }
        return -1;
    }

    public int getPessoas() {
        return pessoas;
    }

    public void acumulaTempo(double tempoAtual, double tempoAnterior) {
        double dif = tempoAtual - tempoAnterior;
        if (pessoas >= 0 && pessoas < tempos.length) {
            tempos[pessoas] += dif;
        }
    }

    public void chegada(double tempoAtual, int filaIndex) {
        if (pessoas < capFila) {
            pessoas++;
            int funcionario = buscaFuncionarioLivre();
            if (funcionario >= 0) {
                double tempoAtend = Simulador.geraTempo(atendimentoMin, atendimentoMax);
                funcionarios[funcionario] = tempoAtual + tempoAtend;
                Simulador.agendarEvento(new Evento(funcionario, filaIndex, funcionarios[funcionario]));
            }
        } else {
            perdaClientes++;
        }
    
        if ((Simulador.numerosUsados < Simulador.quantidadeNumeros) && filaIndex == 0) {
            double proximaChegada = tempoAtual + Simulador.geraTempo(Simulador.chegadaMin, Simulador.chegadaMax);
            Simulador.agendarEvento(new Evento(-1, filaIndex, proximaChegada));
        }
    }
    
    public void saida(int funcionario, double tempoAtual, int filaIndex) {
        pessoas--;
    
        if (filaIndex + 1 < Simulador.filas.length) {
            Simulador.agendarEvento(new Evento(-1, filaIndex + 1, tempoAtual));
        }
    
        if (pessoas >= numFuncionarios) {
            double tempoAtend = Simulador.geraTempo(atendimentoMin, atendimentoMax);
            funcionarios[funcionario] = tempoAtual + tempoAtend;
            Simulador.agendarEvento(new Evento(funcionario, filaIndex, funcionarios[funcionario]));
        } else {
            funcionarios[funcionario] = -1;
        }
    }

    public void printDados(int index, double tempoTotal) {
        System.out.printf("Estados da fila %d:%n", index + 1);
        for (int i = 0; i < tempos.length; i++) {
            System.out.printf("%d: %.4f (%.4f%%)%n", i, tempos[i], (tempos[i] / tempoTotal) * 100);
        }
        System.out.println();
        System.out.println("Perda de clientes: " + perdaClientes);
        System.out.println("------------------------");
    }
}
