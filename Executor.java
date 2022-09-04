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
                    } else if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_INDIRETO_OP1) > 0) { // endereçamento
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
                    if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_INDIRETO_OP1) > 0) { // endereçamento indireto
                        int enderecoDoEndereco = memoria[contadorDePrograma++];
                        contadorDePrograma = memoria[enderecoDoEndereco];
                    } else { // endereçamento direto
                        contadorDePrograma = memoria[contadorDePrograma];
                    }
                    break;

                case Opcodes.BRNEG:
                    if (acumulador < 0) {
                        if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_INDIRETO_OP1) > 0) { // endereçamento indireto
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
                        if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_INDIRETO_OP1) > 0) { // endereçamento indireto
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
                        if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_INDIRETO_OP1) > 0) { // endereçamento indireto
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
                    memoria[ponteiroDaPilha++] = contadorDePrograma + 1;
                    memoria[ponteiroDaPilha++] = acumulador;
                    memoria[ponteiroDaPilha++] = registradorDeInstrucao; // TODO: try without this
                    if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_INDIRETO_OP1) > 0) {
                        int enderecoDoEndereco = memoria[contadorDePrograma];
                        contadorDePrograma = memoria[enderecoDoEndereco];
                    } else {
                        contadorDePrograma = memoria[contadorDePrograma];
                    }
                    checkStackSize();
                    break;

                case Opcodes.RET:
                    registradorDeInstrucao  = memoria[--ponteiroDaPilha];
                    acumulador              = memoria[--ponteiroDaPilha];
                    contadorDePrograma      = memoria[--ponteiroDaPilha];
                    break;

                case Opcodes.COPY:
                    int enderecoDoOp1, op2;
                    // pegar enderecoDoOp1
                    if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_INDIRETO_OP1) > 0) {
                        int enderecoDoEnderecoDoOp1 = memoria[contadorDePrograma++];
                        enderecoDoOp1 = memoria[enderecoDoEnderecoDoOp1];
                    } else { // endereçamento direto
                        enderecoDoOp1 = memoria[contadorDePrograma++];
                    }

                    // pegar op2
                    if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_INDIRETO_OP2) > 0) {
                        int enderecoDoEnderecoDoOp2 = memoria[contadorDePrograma++];
                        int enderecoDoOp2 = memoria[enderecoDoEnderecoDoOp2];
                        op2 = memoria[enderecoDoOp2];
                    } else if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_IMEDIATO) > 0) {
                        op2 = memoria[contadorDePrograma++];
                    } else {
                        int enderecoDoOp2 = memoria[contadorDePrograma++];
                        op2 = memoria[enderecoDoOp2];
                    }

                    memoria[enderecoDoOp1] = op2;
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
        memoria[STACK_LIMIT + 0] = Opcodes.COPY | Bitmasks.ENDERECAMENTO_INDIRETO_OP1 | Bitmasks.ENDERECAMENTO_IMEDIATO;
        memoria[STACK_LIMIT + 1] = STACK_LIMIT + 20;
        memoria[STACK_LIMIT + 2] = 49;
        memoria[STACK_LIMIT + 3] = Opcodes.STOP;

        memoria[STACK_LIMIT + 20] = STACK_LIMIT + 21;
        memoria[STACK_LIMIT + 21] = 659867;

        Executor executor = new Executor(memoria);

        System.out.println("acumulador: " + executor.acumulador);
    }
}

/*
 * To do:
 * -
 */
