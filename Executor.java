import java.io.File;
import java.io.IOException;

class Executor {
    static final int STACK_LIMIT = 100;

    short contadorDePrograma = STACK_LIMIT;
    int acumulador;
    int registradorDeInstrucao;
    Memoria memoria;
    short ponteiroDaPilha = 3;

    boolean terminou = false;

    Executor(Memoria memoria) {
        this.memoria = memoria;
    }

    void step() {
        registradorDeInstrucao = memoria.get(contadorDePrograma++);

        switch (registradorDeInstrucao & 0b1111) {
            case Opcodes.ADD:
                if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_IMEDIATO) > 0) {
                    acumulador += memoria.get(contadorDePrograma++);
                } else if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_INDIRETO_OP1) > 0) {
                    short enderecoDoEndereco = memoria.get(contadorDePrograma++);
                    short endereco = memoria.get(enderecoDoEndereco);
                    acumulador += memoria.get(endereco);
                } else { // endereçamento direto
                    short endereco = memoria.get(contadorDePrograma++);
                    acumulador += memoria.get(endereco);
                }
                break;

            case Opcodes.BR:
                if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_INDIRETO_OP1) > 0) {
                    short enderecoDoEndereco = memoria.get(contadorDePrograma++);
                    contadorDePrograma = memoria.get(enderecoDoEndereco);
                } else { // endereçamento direto
                    contadorDePrograma = memoria.get(contadorDePrograma);
                }
                break;

            case Opcodes.BRNEG:
                if (acumulador < 0) {
                    if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_INDIRETO_OP1) > 0) {
                        short enderecoDoEndereco = memoria.get(contadorDePrograma++);
                        contadorDePrograma = memoria.get(enderecoDoEndereco);
                    } else { // endereçamento direto
                        contadorDePrograma = memoria.get(contadorDePrograma);
                    }
                } else {
                    contadorDePrograma++;
                }
                break;

            case Opcodes.BRPOS:
                if (acumulador > 0) {
                    if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_INDIRETO_OP1) > 0) {
                        short enderecoDoEndereco = memoria.get(contadorDePrograma++);
                        contadorDePrograma = memoria.get(enderecoDoEndereco);
                    } else { // endereçamento direto
                        contadorDePrograma = memoria.get(contadorDePrograma);
                    }
                } else {
                    contadorDePrograma++;
                }
                break;

            case Opcodes.BRZERO:
                if (acumulador == 0) {
                    if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_INDIRETO_OP1) > 0) {
                        short enderecoDoEndereco = memoria.get(contadorDePrograma++);
                        contadorDePrograma = memoria.get(enderecoDoEndereco);
                    } else { // endereçamento direto
                        contadorDePrograma = memoria.get(contadorDePrograma);
                    }
                } else {
                    contadorDePrograma++;
                }
                break;

            case Opcodes.CALL:
                memoria.set(ponteiroDaPilha++, (short) (contadorDePrograma + 1));
                if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_INDIRETO_OP1) > 0) {
                    short enderecoDoEndereco = memoria.get(contadorDePrograma);
                    contadorDePrograma = memoria.get(enderecoDoEndereco);
                } else {
                    contadorDePrograma = memoria.get(contadorDePrograma);
                }
                checkStackSize();
                break;

            case Opcodes.RET:
                contadorDePrograma = memoria.get(--ponteiroDaPilha);
                break;

            case Opcodes.COPY:
                short enderecoDoOp1, op2;
                // pegar enderecoDoOp1
                if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_INDIRETO_OP1) > 0) {
                    short enderecoDoEnderecoDoOp1 = memoria.get(contadorDePrograma++);
                    enderecoDoOp1 = memoria.get(enderecoDoEnderecoDoOp1);
                } else { // endereçamento direto
                    enderecoDoOp1 = memoria.get(contadorDePrograma++);
                }

                // pegar op2
                if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_INDIRETO_OP2) > 0) {
                    short enderecoDoEnderecoDoOp2 = memoria.get(contadorDePrograma++);
                    short enderecoDoOp2 = memoria.get(enderecoDoEnderecoDoOp2);
                    op2 = memoria.get(enderecoDoOp2);
                } else if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_IMEDIATO) > 0) {
                    op2 = memoria.get(contadorDePrograma++);
                } else {
                    short enderecoDoOp2 = memoria.get(contadorDePrograma++);
                    op2 = memoria.get(enderecoDoOp2);
                }

                memoria.set(enderecoDoOp1, op2);
                break;

            case Opcodes.DIVIDE:
                if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_IMEDIATO) > 0) {
                    acumulador = acumulador / memoria.get(contadorDePrograma++);
                } else if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_INDIRETO_OP1) > 0) {
                    short enderecoDoEndereco = memoria.get(contadorDePrograma++);
                    short endereco = memoria.get(enderecoDoEndereco);
                    acumulador = acumulador / memoria.get(endereco);
                } else { // endereçamento direto
                    short endereco = memoria.get(contadorDePrograma++);
                    acumulador = acumulador / memoria.get(endereco);
                }
                break;

            case Opcodes.MULT:
                if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_IMEDIATO) > 0) {
                    acumulador = acumulador * memoria.get(contadorDePrograma++);
                } else if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_INDIRETO_OP1) > 0) {
                    short enderecoDoEndereco = memoria.get(contadorDePrograma++);
                    short endereco = memoria.get(enderecoDoEndereco);
                    acumulador = acumulador * memoria.get(endereco);
                } else { // endereçamento direto
                    short endereco = memoria.get(contadorDePrograma++);
                    acumulador = acumulador * memoria.get(endereco);
                }
                break;

            case Opcodes.SUB:
                if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_IMEDIATO) > 0) {
                    acumulador = acumulador - memoria.get(contadorDePrograma++);
                } else if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_INDIRETO_OP1) > 0) {
                    short enderecoDoEndereco = memoria.get(contadorDePrograma++);
                    short endereco = memoria.get(enderecoDoEndereco);
                    acumulador = acumulador - memoria.get(endereco);
                } else { // endereçamento direto
                    short endereco = memoria.get(contadorDePrograma++);
                    acumulador = acumulador - memoria.get(endereco);
                }
                break;

            case Opcodes.LOAD:
                if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_IMEDIATO) > 0) {
                    acumulador = memoria.get(contadorDePrograma++);
                } else if ((registradorDeInstrucao & Bitmasks.ENDERECAMENTO_INDIRETO_OP1) > 0) {
                    short enderecoDoEndereco = memoria.get(contadorDePrograma++);
                    short endereco = memoria.get(enderecoDoEndereco);
                    acumulador = memoria.get(endereco);
                } else { // endereçamento direto
                    short endereco = memoria.get(contadorDePrograma++);
                    acumulador = memoria.get(endereco);
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
        if (ponteiroDaPilha > memoria.get((short) 2)) {
            System.out.println("Stack overflow.");
            System.exit(1);
        }
    }

    public int getRegistradorDeInstrucao() {
        return registradorDeInstrucao;
    }

    public int getPonteiroDaPilha() {
        return ponteiroDaPilha;
    }

    public int getAcumulador() {
        return acumulador;
    }


    public static void main(String[] args) throws IOException {

        Memoria memoria = new Memoria(1000, new File("exemplos/chamada_add_var.HPX"));

        // memoria.set(STACK_LIMIT + 0, Opcodes.ADD | Bitmasks.ENDERECAMENTO_IMEDIATO);
        // memoria.set(STACK_LIMIT + 1,10);

        // // chama a funcao subtrai1
        // memoria.set(STACK_LIMIT + 2, Opcodes.CALL);
        // memoria.set(STACK_LIMIT + 3, STACK_LIMIT + 50);
        // memoria.set(STACK_LIMIT + 4, Opcodes.STOP);
        
        // // funcao subtrai1
        // memoria.set(STACK_LIMIT + 50, Opcodes.LOAD | Bitmasks.ENDERECAMENTO_INDIRETO_OP1);
        // memoria.set(STACK_LIMIT + 51, STACK_LIMIT + 60);
        // memoria.set(STACK_LIMIT + 52, Opcodes.RET);

        // memoria.set(STACK_LIMIT + 60, STACK_LIMIT + 61);
        // memoria.set(STACK_LIMIT + 61, 56);

        Executor executor = new Executor(memoria);

        while (!executor.terminou) {
            executor.step();
        }

        System.out.println("acumulador: " + executor.getAcumulador());
        System.out.println("ponteiro pilha: " + executor.getPonteiroDaPilha());
        System.out.println("RI: " + executor.getRegistradorDeInstrucao());

    }
}

/*
 * To do:
 * -
 */
