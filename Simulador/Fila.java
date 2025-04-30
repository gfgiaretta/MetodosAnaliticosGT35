package Simulador;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.NoSuchElementException;

public class Fila {
    private String nome;
    private int numFuncionarios;
    private int capFila;
    private double atendimentoMin;
    private double atendimentoMax;
    private ArrayList<ProbabilidadeFila> probFilas;

    private int pessoas = 0;
    private int perdaClientes = 0;
    private double[] tempos;
    private double[] funcionarios;

    public Fila(String nome, int numFuncionarios, int capFila, double atendimentoMin, double atendimentoMax, ArrayList<ProbabilidadeFila> probFilas) {
        this.nome = nome;
        this.numFuncionarios = numFuncionarios;
        this.capFila = capFila;
        this.atendimentoMin = atendimentoMin;
        this.atendimentoMax = atendimentoMax;
        this.probFilas = probFilas;

        if (capFila <= 0) {
            this.capFila = Simulador.quantidadeNumeros;
        }

        this.tempos = new double[this.capFila + 1];
        this.funcionarios = new double[numFuncionarios];
        Arrays.fill(this.funcionarios, -1);
        Collections.sort(probFilas);
    }

    public Fila(String nome, int numFuncionarios, int capFila, double atendimentoMin, double atendimentoMax) {
        this.nome = nome;
        this.numFuncionarios = numFuncionarios;
        this.capFila = capFila;
        this.atendimentoMin = atendimentoMin;
        this.atendimentoMax = atendimentoMax;
        this.probFilas = new ArrayList<>();

        if (capFila <= 0) {
            this.capFila = Simulador.quantidadeNumeros;
        }

        this.tempos = new double[this.capFila + 1];
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

    public String getNome() {
        return nome;
    }

    public int getNumFuncionarios() {
        return numFuncionarios;
    }

    public int getCapacidade() {
        return capFila;
    }

    public double getMinServico() {
        return atendimentoMin;
    }

    public double getMaxServico() {
        return atendimentoMax;
    }

    public ArrayList<ProbabilidadeFila> getProbFilas() {
        return probFilas;
    }

    public void addProbFila(String nomeFila, double probabilidade) {
        if (probabilidade <= 0 || probabilidade > 1) {
            throw new IllegalArgumentException("A probabilidade deve ser maior que 0 e menor ou igual a 1.");
        } 
        double probTotal = probabilidade;
        for (ProbabilidadeFila p : probFilas) {
            probTotal += p.getProbabilidade();
        }
        if (probTotal > 1) {
            throw new IllegalStateException("A soma das probabilidades não pode ser maior que 1.");
        }
        probFilas.add(new ProbabilidadeFila(nomeFila, probabilidade));
        Collections.sort(probFilas);
    }

    public void validarNomesProbFila() {
        for (ProbabilidadeFila p : probFilas) {
            boolean valido = false;
            for (Fila f : Simulador.filas) {
                if (p.getNomeFila().equals(f.getNome())) {
                    valido = true;
                    break;
                }
            }
            if (!valido) {
                throw new NoSuchElementException("A fila de nome '" + p.getNomeFila() + "' não existe.");
            }
        }
    }

    public void acumulaTempo(double tempoAtual, double tempoAnterior) {
        double dif = tempoAtual - tempoAnterior;
        if (pessoas >= 0 && pessoas < tempos.length) {
            tempos[pessoas] += dif;
        }
    }

    public void agendarNovaSaida (int funcionario, String nomeFila, double tempoAtual) {

        Fila fila = Simulador.getFilaPorNome(nomeFila);
        if(fila.probFilas.size() == 1 && fila.probFilas.get(0).getProbabilidade() == 1) {
            double tempoAtend = Simulador.geraTempo(atendimentoMin, atendimentoMax);
            funcionarios[funcionario] = tempoAtual + tempoAtend;
            Simulador.agendarEvento(new Evento(funcionario, fila.nome, fila.probFilas.get(0).getNomeFila(), funcionarios[funcionario]));
        }
        else if (!fila.probFilas.isEmpty()) {
            String filaSorteada = Simulador.sortearFila(fila);
            if (Simulador.numerosUsados < Simulador.quantidadeNumeros) {
                double tempoAtend = Simulador.geraTempo(atendimentoMin, atendimentoMax);
                funcionarios[funcionario] = tempoAtual + tempoAtend;
            }
            Simulador.agendarEvento(new Evento(funcionario, fila.nome, filaSorteada, funcionarios[funcionario]));
        }
        else {
            double tempoAtend = Simulador.geraTempo(atendimentoMin, atendimentoMax);
            funcionarios[funcionario] = tempoAtual + tempoAtend;
            Simulador.agendarEvento(new Evento(funcionario, fila.nome, "", funcionarios[funcionario]));
        }
    }

    public void chegada(double tempoAtual, String nomeFila, boolean deFora) {
        if (pessoas < capFila) {
            pessoas++;
            int funcionario = buscaFuncionarioLivre();
            if (funcionario >= 0) {

                agendarNovaSaida(funcionario, nomeFila, tempoAtual);

            }
        } else {
            perdaClientes++;
        }
    
        if ((Simulador.numerosUsados < Simulador.quantidadeNumeros) && nomeFila.equals(Simulador.nomeFilaChegada) && deFora) {
            double proximaChegada = tempoAtual + Simulador.geraTempo(Simulador.chegadaMin, Simulador.chegadaMax);
            Simulador.agendarEvento(new Evento(-1, "", nomeFila, proximaChegada));
        }
    }
    
    public void saida(int funcionario, double tempoAtual, String nomeFila, String nomeFilaDestino) {
        pessoas--;

        if (pessoas >= numFuncionarios && Simulador.numerosUsados < Simulador.quantidadeNumeros) {
            agendarNovaSaida(funcionario, nomeFila, tempoAtual);
        } else {
            funcionarios[funcionario] = -1;
        }

        if (!nomeFilaDestino.equals("")) {
            Simulador.agendarEvento(new Evento(-1, nomeFila, nomeFilaDestino, tempoAtual));
        }
    }

    public void printDados(int index, double tempoTotal) {
        System.out.printf("Estados da fila %s:%n", nome);
        for (int i = 0; i < tempos.length; i++) {
            if (tempos[i] > 0) {
                System.out.printf("%d: %.4f (%.4f%%)%n", i, tempos[i], (tempos[i] / tempoTotal) * 100);
            }
        }
        System.out.println();
        System.out.println("Perda de clientes: " + perdaClientes);
        System.out.println("------------------------");
    }
}
