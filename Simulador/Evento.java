package Simulador;
public class Evento implements Comparable<Evento> {
    public int tipo; // -1 para chegada, ou índice do funcionário para saída
    public int fila; // índice da fila no vetor de filas
    public double tempoGlobal;

    public Evento(int tipo, int fila, double tempoGlobal) {
        this.tipo = tipo;
        this.fila = fila;
        this.tempoGlobal = tempoGlobal;
    }

    @Override
    public int compareTo(Evento outro) {
        return Double.compare(this.tempoGlobal, outro.tempoGlobal);
    }
}
