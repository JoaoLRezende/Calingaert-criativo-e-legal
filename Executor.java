class Executor {
    
    int contadorDePrograma = 0;
    int acumulador;
    int registradorDeInstrucao;

    Executor(int[] memoria) {
        
        while ((registradorDeInstrucao = memoria[contadorDePrograma++]) != 11) {
            if ((registradorDeInstrucao & 1111) == 02) { // ADD
                if ((registradorDeInstrucao & 0b10000000) > 0) { // endereçamento imediato
                    acumulador += memoria[contadorDePrograma++];
                } else if ((registradorDeInstrucao & 0b100000) > 0) { // endereçamento indireto
                    int endereco_do_endereco = memoria[contadorDePrograma++];
                    int endereco = memoria[endereco_do_endereco];
                    acumulador += memoria[endereco];
                } else { // endereçamento direto
                    int endereco = memoria[contadorDePrograma++];
                    acumulador += memoria[endereco];
                }
        }

    }

    public static void main(String[] args) {
        int[] memoria = new int[10_000];
        memoria[0] =  2 | 0b100000;
        memoria[1] = 3;
        memoria[2] = 11;
        memoria[3] = 4;
        memoria[4] = 49;

        Executor executor = new Executor(memoria);

        System.out.println("acumulador: " + executor.acumulador);
    }
}

