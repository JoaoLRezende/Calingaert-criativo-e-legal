import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.HashMap;

public class Montador {
    File modulo;
    
    // TODO: garantir que os nomes das tabelas abaixo são realmente os nomes usados pelo Ferrugem.

    // Tabela de símbolos internos ao módulo que está sendo montado.
    HashMap<String, Short> tabelaDeSimbolos = new HashMap<>();

    // Tabela de símbolos definidos neste módulo para uso por outros módulos.
    HashMap<String, Short> tabelaDeDefinicoes = new HashMap<>();

    // Tabela de símbolos definidos em outros módulos, mas visíveis neste módulo.
    HashMap<String, Short> tabelaDeUso = new HashMap<>();

    public Montador(File modulo) {
        this.modulo = modulo;
    }

    public void primeiroPasso() {
        Scanner scanner = null;
        try {
            scanner = new Scanner(modulo);
        } catch (FileNotFoundException e) {
            System.out.println("Montador: erro ao criar arquivo objeto.");
            System.exit(1);
        }

        short contadorDePosicao = 0;

        while (scanner.hasNext()) {
            String rotulo = null;
            String opname = null;
            String rotuloOuOpname = scanner.next();
        
            // se houver um rótulo
            if (!Instrucoes.existeOpname(rotuloOuOpname)) {
                rotulo = rotuloOuOpname;
                opname = scanner.next();
                if (tabelaDeDefinicoes.containsKey(rotulo)) {
                    tabelaDeDefinicoes.put(rotulo, contadorDePosicao);
                } else {
                    tabelaDeSimbolos.put(rotulo, contadorDePosicao);
                }
            } else {
                opname = rotuloOuOpname;

                if (opname.equals("EXTDEF")) {
                    tabelaDeDefinicoes.put(scanner.next(), (short) -1);
                }

                if (opname.equals("EXTR")) {
                    tabelaDeUso.put(scanner.next(), (short) -1);
                }
            }

            InstrucaoDados instrucao = Instrucoes.getInstrucao(opname);

            contadorDePosicao += instrucao.tamanho;

            scanner.nextLine();
        }

        scanner.close();
    }

    public void segundoPasso() {
        Scanner scanner = null;
        try {
            scanner = new Scanner(modulo);
        } catch (FileNotFoundException e) {
            System.out.println("Montador: erro ao criar arquivo objeto.");
            System.exit(1);
        }

        File arquivoObjeto = new File("esperanca.obj");
        try {
            arquivoObjeto.createNewFile();
        } catch (IOException e) {
            System.out.println("Montador: erro ao criar arquivo objeto.");
            System.exit(1);
        }

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(arquivoObjeto);
        } catch (FileNotFoundException e) {
            System.out.println("Montador: erro ao criar arquivo objeto.");
            System.exit(1);
        }

        short contadorDePosicao = 0;

        main_loop:
        while (scanner.hasNext()) {
            String rotulo = null;
            String opname = null;
            String rotuloOuOpname = scanner.next();
        
            // se houver um rótulo
            if (!Instrucoes.existeOpname(rotuloOuOpname)) {
                rotulo = rotuloOuOpname;
                opname = scanner.next();
            } else {
                opname = rotuloOuOpname;
            }

            InstrucaoDados instrucao = Instrucoes.getInstrucao(opname);
            OperandoInfo operandoInfo = null;

            switch (instrucao) {
                case ADD:
                case BR:
                case BRNEG:
                case BRPOS:
                case BRZERO:
                case CALL:
                case DIVIDE:
                case MULT:
                case READ:
                case LOAD:
                case STORE:
                case SUB:
                case WRITE:
                    operandoInfo = processarOperando(scanner.next());
                    escreverShort(outputStream, instrucao.opcode | operandoInfo.modoDeEnderecamento);
                    escreverShort(outputStream, operandoInfo.operando);
                    contadorDePosicao += 2;
                    break;
                
                case COPY:
                    OperandoInfo operando1Info = processarOperando(scanner.next());
                    OperandoInfo operando2Info = processarOperando(scanner.next());
                    if (operando2Info.modoDeEnderecamento == Bitmasks.ENDERECAMENTO_INDIRETO_OP1) {
                        operando2Info.modoDeEnderecamento = Bitmasks.ENDERECAMENTO_INDIRETO_OP2;
                    }

                    escreverShort(outputStream, instrucao.opcode | operando1Info.modoDeEnderecamento
                                                                 | operando2Info.modoDeEnderecamento);
                    escreverShort(outputStream, operando1Info.operando);
                    escreverShort(outputStream, operando2Info.operando);
                    contadorDePosicao += 3;
                    break;

                case STOP:
                case RET:
                    escreverShort(outputStream, instrucao.opcode);
                    contadorDePosicao += 1;
                    break;
                
                case CONST:
                    operandoInfo = processarOperando(scanner.next());
                    escreverShort(outputStream, operandoInfo.operando);
                    contadorDePosicao += 1;
                    break;

                case SPACE:
                    escreverShort(outputStream, 0);
                    contadorDePosicao += 1;
                    break;

                case EXTDEF:
                    break;

                case EXTR:
                    break;

                case END:
                    break main_loop;
            }


            scanner.nextLine();
        }

        scanner.close();
    }

    private void escreverShort(FileOutputStream stream, int num) {
        short num2 = (short) num;
        try {
            stream.write((byte) (num2 >> 8));
            stream.write((byte) num2);
        } catch (IOException e) {
            System.out.println("Montador: erro em escrever arquivo objeto.");
            System.exit(1);
        }
    }

    class OperandoInfo {
        short modoDeEnderecamento;
        int operando;

        public OperandoInfo(short modoDeEnderecamento, int operando) {
            this.modoDeEnderecamento = modoDeEnderecamento;
            this.operando = operando;
        }
    }

    Short pegaEnderecoDeSimbolo(String simbolo) {
        if (tabelaDeSimbolos.containsKey(simbolo)) {
            return tabelaDeSimbolos.get(simbolo);
        } else if (tabelaDeDefinicoes.containsKey(simbolo)) {
            return tabelaDeDefinicoes.get(simbolo);
        } else if (tabelaDeUso.containsKey(simbolo)) {
            return tabelaDeUso.get(simbolo);
        }

        return -1;
    }

    OperandoInfo processarOperando(String operando) {
        switch (operando.charAt(0)) {
            case '@':   // operando imediato
                return new OperandoInfo(Bitmasks.ENDERECAMENTO_IMEDIATO, Integer.parseInt(operando.substring(1)));
            case '&':   // endereçamento indireto
                return new OperandoInfo(Bitmasks.ENDERECAMENTO_INDIRETO_OP1, pegaEnderecoDeSimbolo(operando.substring(1)));
            default:    // um rótulo (endereçamento direto)
                return new OperandoInfo((short) 0, pegaEnderecoDeSimbolo(operando));
        }
    }

    public static void main(String[] args) {
        Montador montador = new Montador(new File("input.txt"));
        montador.primeiroPasso();
        System.out.println("Tabela de símbolos: "   + montador.tabelaDeSimbolos.toString());
        System.out.println("Tabela de definições: " + montador.tabelaDeDefinicoes.toString());
        System.out.println("Tabela de uso: "        + montador.tabelaDeUso.toString());
        montador.segundoPasso();
    }
}
