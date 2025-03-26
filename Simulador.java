import java.util.Arrays;

public class Simulador {

 /*VARIÁVEIS EDITÁVEIS
    Estas são as variáveis que podem ser editadas pelo usuário para alterar os resultados da simulação.
 */
    static int capFila = 5; //Capacidade da fila;
    static int numFuncionarios = 1; //Número de funcionários atendendo a fila;

    static double chegadaMin = 2; //Tempo mínimo de chegada
    static double chegadaMax = 5; //Tempo máximo de chegada

    static double atendimentoMin = 3; //Tempo mínimo de atendimento;
    static double atendimentoMax = 5; //Tempo máximo de atendimento;

    static double primeiraChegada = 2; //Tempo da primeira chegada;

    static long a = 1675893;
    static long c = 3345554;
    static long M = 6574367296L;
    static long seed = 3;

    static int quantidadeNumeros = 100000; //Quantidade de números pseudoaleatórios a serem gerados;

 /*VARIÁVEIS NÃO-EDITÁVEIS
    Estas são as variáveis que são utilizadas e modificadas no decorrer do programa, não sendo modificadas pelo usuário.
 */
    static double proxChegada; //Tempo da próxima chegada;
    static double[] tempos = new double[capFila + 1]; //Vetor que armazena o tempo, até o momento, de cada estado da fila;
    static double[] funcionarios = new double[numFuncionarios]; //Vetor que armazena o tempo em que cada funcionário finaliza seu atendimento (ou -1 se o funcionário está livre);
    static double[] numeros = new double[quantidadeNumeros]; //Vetor que armazena os números pseudoaleatórios;
    static int fila; //Número de pessoas na fila;
    static int perdaClientes; //Número de clientes perdidos;
    static int numerosUsados; //Número de pseudoaleatórios usados até o momento;
    static double tempoGlobal; //Tempo global da simulação;

    public static double[] gerador () {
        long s = seed;
        for (int i = 0; i < quantidadeNumeros; i++) {
            s = (a * s + c) % M;
            double normalized = (double) s / M;
            numeros[i] = normalized;
        }
        return numeros;
    }

    /*
    Se retornar -1 ---------------> Próximo evento = CHEGADA.
    Se retornar outro número n ---> Próximo evento = SAÍDA do cliente sendo atendido pelo funcionário n.
    */
    public static int nextEvent() {
        Integer menorIndice = null;
        double menorTempo = Double.MAX_VALUE;
        
        for (int i = 0; i < funcionarios.length; i++) {
            if (Double.isNaN(funcionarios[i]) || funcionarios[i] < 0) {
                continue;
            }
            if (funcionarios[i] < menorTempo) {
                menorTempo = funcionarios[i];
                menorIndice = i;
            }
        }
        
        if (menorIndice == null || menorTempo >= proxChegada) {
            return -1;
        }
        return menorIndice;
    }

    public static void acumulaTempo(double tempoEvento) {
        double difTempo = tempoEvento - tempoGlobal;
        tempos[fila] += difTempo;
    }

    public static int buscaFuncionario() {
        for (int i = 0; i < numFuncionarios; i++) {
            if (funcionarios[i] == -1) {
                return i;
            }
        }
        return -1;
    }

    public static double geraTempo(double min, double max) {
        double res = min + ((max - min)*numeros[numerosUsados]);
        numerosUsados++;
        return res;
    }

    public static void chegada() {
        acumulaTempo(proxChegada);
        tempoGlobal = proxChegada;
        if (fila < capFila) {
            fila++;
            int f = buscaFuncionario();
            if (f >= 0) {
                funcionarios[f] = tempoGlobal + geraTempo(atendimentoMin, atendimentoMax);
            }
        }
        else perdaClientes++;
        if (numerosUsados < quantidadeNumeros) {
            proxChegada = tempoGlobal + geraTempo(chegadaMin, chegadaMax);
        }
    }

    public static void saida(int posFuncionario) {
        acumulaTempo(funcionarios[posFuncionario]);
        tempoGlobal = funcionarios[posFuncionario];
        fila--;
        if (fila >= numFuncionarios) {
            funcionarios[posFuncionario] = tempoGlobal + geraTempo(atendimentoMin, atendimentoMax);
        }
        else funcionarios[posFuncionario] = -1;
    }

    public static void printDados() {
        System.out.println("Estados da fila:");
        for (int i = 0; i < tempos.length; i++) {
            System.out.printf("%d: %.4f (%.4f%%)%n", i, tempos[i], (tempos[i] / tempoGlobal) * 100);
        }
        System.out.println("------------------------");
        System.out.println("Perda de clientes: " + perdaClientes);
        System.out.println("------------------------");
        System.out.printf("Tempo de simulaçao: %.4f%n", tempoGlobal);
    }

    public static void main (String args[]) {
        proxChegada = primeiraChegada;
        fila = 0;
        perdaClientes = 0;
        numerosUsados = 0;
        tempoGlobal = 0;
        Arrays.fill(tempos, 0);
        Arrays.fill(funcionarios, -1);

        gerador();

        while (numerosUsados < quantidadeNumeros) {
            int evento = nextEvent();

            if (evento == -1) {
                chegada();
            }
            else if (evento >= 0) {
                saida(evento);
            }
        }

        printDados();
    }

}
