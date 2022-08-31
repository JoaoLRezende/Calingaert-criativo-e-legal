class Executor {
    
    int contadorDePrograma = 0;
    int acumulador;
    int registradorDeInstrucao;

    Executor(int[] memoria) {
        
        while ((registradorDeInstrucao = memoria[contadorDePrograma++]) != 11) {
            switch (registradorDeInstrucao & 1111) {
                case 2: // ADD
                    if ((registradorDeInstrucao & 0b10000000) > 0) { // endereçamento imediato
                        acumulador += memoria[contadorDePrograma++];
                    } else if ((registradorDeInstrucao & 0b100000) > 0) { // endereçamento indireto
                        int enderecoDoEndereco = memoria[contadorDePrograma++];
                        int endereco = memoria[enderecoDoEndereco];
                        acumulador += memoria[endereco];
                    } else { // endereçamento direto
                        int endereco = memoria[contadorDePrograma++];
                        acumulador += memoria[endereco];
                    }
                    break;
                case 0: // BR
                    if ((registradorDeInstrucao & 0b100000) > 0) { // endereçamento indireto
                        int enderecoDoEndereco = memoria[contadorDePrograma++];
                        contadorDePrograma = memoria[enderecoDoEndereco];
                    } else { // endereçamento direto
                        contadorDePrograma = memoria[contadorDePrograma];
                    }
                    break;
                default:
                    System.out.println("Instrução não implementada.");
                    System.exit(1);
            }
        }

    }

    public static void main(String[] args) {
        int[] memoria = new int[10_000];
        memoria[0] = 0 | 0b100000;
        memoria[1] = 5;
        memoria[2] = 2 | 0b10000000;
        memoria[3] = 49;
        memoria[4] = 11;
        memoria[5] = 2;

        Executor executor = new Executor(memoria);

        System.out.println("acumulador: " + executor.acumulador);
    }
}

