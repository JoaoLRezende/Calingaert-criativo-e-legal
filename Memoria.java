public class Memoria {
    
    //definir:
    // tamanho, vetor da memória.

    //funções:
    // de ler e escrever na memória. 
    // modificar tamanho da memória. 

    //funções de apoio:
    //pegar endereço da memória
    private int memTamanho = 10000;
    private int[] memoria;
    static final int STACK_LIMIT = 100;

    Memoria(int numPalavras){
        memTamanho = numPalavras;
        memoria = new int[memTamanho];
        memoria[2] = STACK_LIMIT;
    }

    public int[] printMemoria() {
        return memoria;
    }

    public void set(int endereco, int conteudo) { 
            memoria[endereco] = conteudo;
    }

    public int get(int endereco){
        return memoria[endereco];
    }

}

