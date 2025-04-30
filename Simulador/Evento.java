package Simulador;
public class Evento implements Comparable<Evento> {
    public int tipo; // -1 para chegada, ou índice do funcionário para saída
    public String nomeFila;
    public String nomeFilaDestino; //usado somente para saídas
    public double tempoGlobal;

    public Evento(int tipo, String nomeFila, double tempoGlobal) {
        this.tipo = tipo;
        this.nomeFila = nomeFila;
        this.tempoGlobal = tempoGlobal;
    }

    public Evento(int tipo, String nomeFila, String nomeFileDestino, double tempoGlobal) {
        this.tipo = tipo;
        this.nomeFila = nomeFila;
        this.nomeFilaDestino = nomeFileDestino;
        this.tempoGlobal = tempoGlobal;
    }

    @Override
    public int compareTo(Evento outro) {
        return Double.compare(this.tempoGlobal, outro.tempoGlobal);
    }
}
