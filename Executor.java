class Executor {
    static final int STACK_LIMIT = 100;

    int contadorDePrograma = STACK_LIMIT;
    int acumulador;
    int registradorDeInstrucao;
    int ponteiroDaPilha = 3;

    boolean terminou = false;

    int[] memoria;

    Executor(int[] memoria) {
        this.memoria = memoria;
        memoria[2] = STACK_LIMIT;
    }

    void step() {
        registradorDeInstrucao = memoria[contadorDePrograma++];

        switch (registradorDeInstrucao & 0b1111) {
            case Opcodes.ADD:
                if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_IMEDIATO) > 0) {
                    acumulador += memoria[contadorDePrograma++];
                } else if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_INDIRETO_OP1) > 0) {
                    int enderecoDoEndereco = memoria[contadorDePrograma++];
                    int endereco = memoria[enderecoDoEndereco];
                    acumulador += memoria[endereco];
                } else { // endereçamento direto
                    int endereco = memoria[contadorDePrograma++];
                    acumulador += memoria[endereco];
                }
                break;

            case Opcodes.BR:
                if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_INDIRETO_OP1) > 0) {
                    int enderecoDoEndereco = memoria[contadorDePrograma++];
                    contadorDePrograma = memoria[enderecoDoEndereco];
                } else { // endereçamento direto
                    contadorDePrograma = memoria[contadorDePrograma];
                }
                break;

            case Opcodes.BRNEG:
                if (acumulador < 0) {
                    if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_INDIRETO_OP1) > 0) {
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
                    if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_INDIRETO_OP1) > 0) {
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
                    if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_INDIRETO_OP1) > 0) {
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
                if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_INDIRETO_OP1) > 0) {
                    int enderecoDoEndereco = memoria[contadorDePrograma];
                    contadorDePrograma = memoria[enderecoDoEndereco];
                } else {
                    contadorDePrograma = memoria[contadorDePrograma];
                }
                checkStackSize();
                break;

            case Opcodes.RET:
                contadorDePrograma = memoria[--ponteiroDaPilha];
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

            case Opcodes.DIVIDE:
                if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_IMEDIATO) > 0) {
                    acumulador = acumulador / memoria[contadorDePrograma++];
                } else if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_INDIRETO_OP1) > 0) {
                    int enderecoDoEndereco = memoria[contadorDePrograma++];
                    int endereco = memoria[enderecoDoEndereco];
                    acumulador = acumulador / memoria[endereco];
                } else { // endereçamento direto
                    int endereco = memoria[contadorDePrograma++];
                    acumulador = acumulador / memoria[endereco];
                }
                break;

            case Opcodes.MULT:
                if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_IMEDIATO) > 0) {
                    acumulador = acumulador * memoria[contadorDePrograma++];
                } else if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_INDIRETO_OP1) > 0) {
                    int enderecoDoEndereco = memoria[contadorDePrograma++];
                    int endereco = memoria[enderecoDoEndereco];
                    acumulador = acumulador * memoria[endereco];
                } else { // endereçamento direto
                    int endereco = memoria[contadorDePrograma++];
                    acumulador = acumulador * memoria[endereco];
                }
                break;

            case Opcodes.SUB:
                if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_IMEDIATO) > 0) {
                    acumulador = acumulador - memoria[contadorDePrograma++];
                } else if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_INDIRETO_OP1) > 0) {
                    int enderecoDoEndereco = memoria[contadorDePrograma++];
                    int endereco = memoria[enderecoDoEndereco];
                    acumulador = acumulador - memoria[endereco];
                } else { // endereçamento direto
                    int endereco = memoria[contadorDePrograma++];
                    acumulador = acumulador - memoria[endereco];
                }
                break;

            case Opcodes.LOAD:
                if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_IMEDIATO) > 0) {
                    acumulador = memoria[contadorDePrograma++];
                } else if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_INDIRETO_OP1) > 0) {
                    int enderecoDoEndereco = memoria[contadorDePrograma++];
                    int endereco = memoria[enderecoDoEndereco];
                    acumulador = memoria[endereco];
                } else { // endereçamento direto
                    int endereco = memoria[contadorDePrograma++];
                    acumulador = memoria[endereco];
                }
                break;

            case Opcodes.STOP:
                terminou = true;
                break;

            default:
                System.out.println("Instrução não implementada.");
                System.exit(1);
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
        memoria[STACK_LIMIT + 4] = Opcodes.STOP;
        
        // funcao subtrai1
        memoria[STACK_LIMIT + 50] = Opcodes.LOAD | Bitmasks.ENDERECAMENTO_INDIRETO_OP1;
        memoria[STACK_LIMIT + 51] = STACK_LIMIT + 60;
        memoria[STACK_LIMIT + 52] = Opcodes.RET;

        memoria[STACK_LIMIT + 60] = STACK_LIMIT + 61;
        memoria[STACK_LIMIT + 61] = 67;

        Executor executor = new Executor(memoria);

        while (!executor.terminou) {
            executor.step();
        }

        System.out.println("acumulador: " + executor.acumulador);
    }
}

/*
 * To do:
 * -
 */
