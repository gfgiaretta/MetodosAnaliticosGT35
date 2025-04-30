package Simulador;
public class Evento implements Comparable<Evento> {
    public int tipo; // -1 para chegada, ou índice do funcionário para saída
    public String nomeFilaOrigem;
    public String nomeFilaDestino;
    public double tempoGlobal;

    public Evento(int tipo, String nomeFilaOrigem, String nomeFileDestino, double tempoGlobal) {
        this.tipo = tipo;
        this.nomeFilaOrigem = nomeFilaOrigem;
        this.nomeFilaDestino = nomeFileDestino;
        this.tempoGlobal = tempoGlobal;
    }

    @Override
    public int compareTo(Evento outro) {
        return Double.compare(this.tempoGlobal, outro.tempoGlobal);
    }
}
