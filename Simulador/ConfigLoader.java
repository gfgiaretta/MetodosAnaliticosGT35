package Simulador;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import org.yaml.snakeyaml.Yaml;

public class ConfigLoader {

    public static void carregarParametros(String caminho) throws Exception {
        Yaml yaml = new Yaml();
        InputStream inputStream = new FileInputStream(caminho);
        Map<String, Object> parametros = yaml.load(inputStream);

        Simulador.a = ((Number) parametros.get("a")).longValue();
        Simulador.c = ((Number) parametros.get("c")).longValue();
        Simulador.M = ((Number) parametros.get("M")).longValue();
        Simulador.seed = ((Number) parametros.get("seed")).longValue();
        Simulador.quantidadeNumeros = ((Number) parametros.get("rndnumbers")).intValue();
        Simulador.nomeFilaChegada = (String) parametros.get("arrivalQueue");
        Simulador.primeiraChegada = ((Number) parametros.get("firstArrival")).doubleValue();
        Simulador.chegadaMin = ((Number) parametros.get("minArrival")).doubleValue();
        Simulador.chegadaMax = ((Number) parametros.get("maxArrival")).doubleValue();

        @SuppressWarnings("unchecked")
        Map<String, Map<String, Object>> filas = (Map<String, Map<String, Object>>) parametros.get("queues");
        List<Fila> listaFilas = new ArrayList<>();

        for (Map.Entry<String, Map<String, Object>> entry : filas.entrySet()) {
            String nome = entry.getKey();
            Map<String, Object> props = entry.getValue();
            int servidores = (int) props.get("servers");
            int capacidade = props.containsKey("capacity") ? ((Number) props.get("capacity")).intValue() : -1;
            double minServ = ((Number) props.get("minService")).doubleValue();
            double maxServ = ((Number) props.get("maxService")).doubleValue();
            listaFilas.add(new Fila(nome, servidores, capacidade, minServ, maxServ));
        }

        Simulador.filas = listaFilas.toArray(new Fila[0]);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rede = (List<Map<String, Object>>) parametros.get("network");
        for (Map<String, Object> r : rede) {
            String origem = (String) r.get("source");
            String destino = (String) r.get("target");
            double prob = ((Number) r.get("probability")).doubleValue();
            Fila origemFila = Simulador.getFilaPorNome(origem);
            origemFila.addProbFila(destino, prob);
        }
    }
}
