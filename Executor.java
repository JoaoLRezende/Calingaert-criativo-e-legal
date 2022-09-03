class Executor {
    
    int contadorDePrograma = 0;
    int acumulador;
    int registradorDeInstrucao;

    Executor(int[] memoria) {
        
        while ((registradorDeInstrucao = memoria[contadorDePrograma++]) != Opcodes.STOP) {
            // TODO: this "1111" below should've been a "0b1111". (How did
            // this even work?) Fix then test.
            switch (registradorDeInstrucao & 1111) {
                case Opcodes.ADD:
                    if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_IMEDIATO) > 0) { // endereçamento imediato
                        acumulador += memoria[contadorDePrograma++];
                    } else if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_INDIRETO) > 0) { // endereçamento indireto
                        int enderecoDoEndereco = memoria[contadorDePrograma++];
                        int endereco = memoria[enderecoDoEndereco];
                        acumulador += memoria[endereco];
                    } else { // endereçamento direto
                        int endereco = memoria[contadorDePrograma++];
                        acumulador += memoria[endereco];
                    }
                    break;

                case Opcodes.BR:
                    if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_INDIRETO) > 0) { // endereçamento indireto
                        int enderecoDoEndereco = memoria[contadorDePrograma++];
                        contadorDePrograma = memoria[enderecoDoEndereco];
                    } else { // endereçamento direto
                        contadorDePrograma = memoria[contadorDePrograma];
                    }
                    break;

                case Opcodes.BRNEG:
                    if (acumulador < 0) {
                        if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_INDIRETO) > 0) { // endereçamento indireto
                            int enderecoDoEndereco = memoria[contadorDePrograma++];
                            contadorDePrograma = memoria[enderecoDoEndereco];
                        } else { // endereçamento direto
                            contadorDePrograma = memoria[contadorDePrograma];
                        }
                    } else {
                        contadorDePrograma++;
                    }
                    break;

                case Opcodes.BRPOS:
                    if (acumulador > 0) {
                        if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_INDIRETO) > 0) { // endereçamento indireto
                            int enderecoDoEndereco = memoria[contadorDePrograma++];
                            contadorDePrograma = memoria[enderecoDoEndereco];
                        } else { // endereçamento direto
                            contadorDePrograma = memoria[contadorDePrograma];
                        }
                    } else {
                        contadorDePrograma++;
                    }
                    break;
                    
                case Opcodes.BRZERO:
                    if (acumulador == 0) {
                        if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_INDIRETO) > 0) { // endereçamento indireto
                            int enderecoDoEndereco = memoria[contadorDePrograma++];
                            contadorDePrograma = memoria[enderecoDoEndereco];
                        } else { // endereçamento direto
                            contadorDePrograma = memoria[contadorDePrograma];
                        }
                    } else {
                        contadorDePrograma++;
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
        memoria[0] = Opcodes.ADD | Bitmasks.ENDERECAMENTO_IMEDIATO;
        memoria[1] = 1;
        memoria[2] = Opcodes.BRZERO | Bitmasks.ENDERECAMENTO_INDIRETO;
        memoria[3] = 7;
        memoria[4] = Opcodes.ADD | Bitmasks.ENDERECAMENTO_IMEDIATO;
        memoria[5] = 49;
        memoria[6] = Opcodes.STOP;
        memoria[7] = 4;
        
        Executor executor = new Executor(memoria);

        System.out.println("acumulador: " + executor.acumulador);
    }
}

/* To do:
 * - 
 */
