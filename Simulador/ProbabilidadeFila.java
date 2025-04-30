package Simulador;

public class ProbabilidadeFila implements Comparable<ProbabilidadeFila> {
    
    private String nomeFila;
    private double probabilidade;

    public ProbabilidadeFila(String nomeFila, double probabilidade) {
        this.nomeFila = nomeFila;
        this.probabilidade = probabilidade;
    }

    public String getNomeFila() {
        return nomeFila;
    }

    public double getProbabilidade() {
        return probabilidade;
    }

    @Override
    public int compareTo(ProbabilidadeFila outra) {
        return Double.compare(this.probabilidade, outra.probabilidade);
    }
}
