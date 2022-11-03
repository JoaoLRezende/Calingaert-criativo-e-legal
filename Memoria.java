public class Memoria {
    
    private int memTamanho = 10000;
    private short[] memoria;
    static final int STACK_LIMIT = 100;

    Memoria(int numPalavras){
        memTamanho = numPalavras;
        memoria = new short[memTamanho];
        memoria[2] = STACK_LIMIT;
    }

    public short[] printMemoria() {
        return memoria;
    }

    public void set(short endereco, short conteudo) { 
            memoria[endereco] = conteudo;
    }

    public short get(short endereco){
        return memoria[endereco];
    }

}

