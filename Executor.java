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

                case 5: // BRNEG
                    if (acumulador < 0) {
                        if ((registradorDeInstrucao & 0b100000) > 0) { // endereçamento indireto
                            int enderecoDoEndereco = memoria[contadorDePrograma++];
                            contadorDePrograma = memoria[enderecoDoEndereco];
                        } else { // endereçamento direto
                            contadorDePrograma = memoria[contadorDePrograma];
                        }
                    } else {
                        contadorDePrograma++;
                    }
                    break;

                case 1: // BRPOS
                    if (acumulador > 0) {
                        if ((registradorDeInstrucao & 0b100000) > 0) { // endereçamento indireto
                            int enderecoDoEndereco = memoria[contadorDePrograma++];
                            contadorDePrograma = memoria[enderecoDoEndereco];
                        } else { // endereçamento direto
                            contadorDePrograma = memoria[contadorDePrograma];
                        }
                    } else {
                        contadorDePrograma++;
                    }
                    break;
                    
                case 4: // BRZERO
                    if (acumulador == 0) {
                        if ((registradorDeInstrucao & 0b100000) > 0) { // endereçamento indireto
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
        memoria[0] = 2 | 0b10000000;
        memoria[1] = 1;
        memoria[2] = 4 | 0b100000;
        memoria[3] = 7;
        memoria[4] = 2 | 0b10000000;
        memoria[5] = 49;
        memoria[6] = 11;
        memoria[7] = 4;
        
        Executor executor = new Executor(memoria);

        System.out.println("acumulador: " + executor.acumulador);
    }
}

/* To do:
 * - definir e usar nomes simbólicos para os códigos de operação (com um enum,
 *   probably) e usá-los no switch. Também usá-los no código de teste.
 * - definir e usar nomes simbólicos para as máscaras que ativam os modos de
 *   endereçamento.
 */
