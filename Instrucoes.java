import java.util.HashMap;

public class Instrucoes {
    static final private HashMap<String, InstrucaoDados> mapa = new HashMap<>();

    static {
        mapa.put("ADD", InstrucaoDados.ADD);
        mapa.put("BR", InstrucaoDados.BR);
        mapa.put("BRNEG", InstrucaoDados.BRNEG);
        mapa.put("BRPOS", InstrucaoDados.BRPOS);
        mapa.put("BRZERO", InstrucaoDados.BRZERO);
        mapa.put("CALL", InstrucaoDados.CALL);
        mapa.put("COPY", InstrucaoDados.COPY);
        mapa.put("DIVIDE", InstrucaoDados.DIVIDE);
        mapa.put("LOAD", InstrucaoDados.LOAD);
        mapa.put("MULT", InstrucaoDados.MULT);
        mapa.put("READ", InstrucaoDados.READ);
        mapa.put("RET", InstrucaoDados.RET);
        mapa.put("STOP", InstrucaoDados.STOP);
        mapa.put("STORE", InstrucaoDados.STORE);
        mapa.put("SUB", InstrucaoDados.SUB);
        mapa.put("WRITE", InstrucaoDados.WRITE);
        mapa.put("CONST", InstrucaoDados.CONST);
        mapa.put("END", InstrucaoDados.END);
        mapa.put("EXTDEF", InstrucaoDados.EXTDEF);
        mapa.put("EXTR", InstrucaoDados.EXTR);
        mapa.put("SPACE", InstrucaoDados.SPACE);
        mapa.put("STACK", InstrucaoDados.STACK);
        mapa.put("START", InstrucaoDados.START);
    }

    static InstrucaoDados getInstrucao(String nomeDaInstrucao) {
        return mapa.get(nomeDaInstrucao);
    }

    static boolean existeOpname(String opname) {
        return mapa.containsKey(opname);
    }
}
