import java.util.HashMap;

public class Instrucoes {
    static final private HashMap<String, InstrucaoDados> mapa = new HashMap<>();

    static {
        mapa.put("ADD", InstrucaoDados.ADD);
    }

    static InstrucaoDados getInstrucao(String nomeDaInstrucao) {
        return mapa.get(nomeDaInstrucao);
    }

    static boolean existeOpname(String opname) {
        return mapa.containsKey(opname);
    }
}
