class Executor {
    static final int STACK_LIMIT = 100;

    int contadorDePrograma = STACK_LIMIT;
    int acumulador;
    int registradorDeInstrucao;
    int ponteiroDaPilha = 3;

    int[] memoria;

    Executor(int[] memoria) {
        this.memoria = memoria;
        memoria[2] = STACK_LIMIT;

        while ((registradorDeInstrucao = memoria[contadorDePrograma++]) != Opcodes.STOP) {
            switch (registradorDeInstrucao & 0b1111) {
                case Opcodes.ADD:
                    if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_IMEDIATO) > 0) { // endereçamento imediato
                        acumulador += memoria[contadorDePrograma++];
                    } else if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_INDIRETO) > 0) { // endereçamento
                                                                                                 // indireto
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

                case Opcodes.CALL:
                    if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_INDIRETO) > 0) {
                        memoria[ponteiroDaPilha] = contadorDePrograma + 1;
                        ponteiroDaPilha++;
                        int enderecoDoEndereco = memoria[contadorDePrograma];
                        contadorDePrograma = memoria[enderecoDoEndereco];
                    } else {
                        memoria[ponteiroDaPilha] = contadorDePrograma + 1;
                        ponteiroDaPilha++;
                        contadorDePrograma = memoria[contadorDePrograma];
                    }
                    checkStackSize();
                    break;

                case Opcodes.RET:
                    ponteiroDaPilha--;
                    contadorDePrograma = memoria[ponteiroDaPilha];
                    break;

                default:
                    System.out.println("Instrução não implementada.");
                    System.exit(1);
            }
        }
    }

    private void checkStackSize() {
        if (ponteiroDaPilha > memoria[2]) {
            System.out.println("Stack overflow.");
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        int[] memoria = new int[10_000];
        memoria[STACK_LIMIT + 0] = Opcodes.ADD | Bitmasks.ENDERECAMENTO_IMEDIATO;
        memoria[STACK_LIMIT + 1] = 10;

        // chama a funcao subtrai1
        memoria[STACK_LIMIT + 2] = Opcodes.CALL;
        memoria[STACK_LIMIT + 3] = STACK_LIMIT + 50;
        memoria[STACK_LIMIT + 4] = Opcodes.BRPOS;
        memoria[STACK_LIMIT + 5] = STACK_LIMIT + 2;
        memoria[STACK_LIMIT + 6] = Opcodes.STOP;
        
        // funcao subtrai1
        memoria[STACK_LIMIT + 50] = Opcodes.ADD | Bitmasks.ENDERECAMENTO_IMEDIATO;
        memoria[STACK_LIMIT + 51] = -1;
        memoria[STACK_LIMIT + 52] = Opcodes.RET;

        Executor executor = new Executor(memoria);

        System.out.println("acumulador: " + executor.acumulador);
    }
}

/*
 * To do:
 * -
 */
