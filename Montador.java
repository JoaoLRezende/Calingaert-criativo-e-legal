import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;

public class Montador {
    File modulo;

    // TODO: garantir que os nomes das tabelas abaixo são realmente os nomes usados pelo Ferrugem.

    // Tabela de símbolos internos ao módulo que está sendo montado.
    HashMap<String, Simbolo> tabelaDeSimbolos = new HashMap<>();

    // Tabela de símbolos definidos neste módulo para uso por outros módulos.
    HashMap<String, Simbolo> tabelaDeDefinicoes = new HashMap<>();

    // Tabela de símbolos definidos em outros módulos, mas visíveis neste módulo.
    HashSet<String> simbolosExternos = new HashSet<>();

    // Tabela de referências a símbolos externos.
    HashMap<String, Short> tabelaDeUsos = new HashMap<>();

    ArrayList<Boolean> mapaDeRelocacao = new ArrayList<>();

    String arquivoDeEntrada;

    public Montador(String arquivoDeEntrada) {
        this.arquivoDeEntrada = arquivoDeEntrada;
        this.modulo = new File(arquivoDeEntrada);

        if(!verificaTamanhoLinhas(modulo)) {
            System.out.println("Erro: módulo de entrada tem linha maior que 80 caracteres.");
            System.exit(1);
        }
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
        
            if (rotuloOuOpname.equals("*")) {
                scanner.nextLine();
                continue;
            }

            // se houver um rótulo
            if (!Instrucoes.existeOpname(rotuloOuOpname)) {
                rotulo = rotuloOuOpname;
                opname = scanner.next();
                if (tabelaDeDefinicoes.containsKey(rotulo)) {
                    tabelaDeDefinicoes.put(rotulo, new Simbolo(contadorDePosicao, Simbolo.ModoDeRelocabilidade.RELATIVO));
                } else {
                    tabelaDeSimbolos.put(rotulo, new Simbolo(contadorDePosicao, Simbolo.ModoDeRelocabilidade.RELATIVO));
                }
            } else {
                opname = rotuloOuOpname;

                if (opname.equals("EXTDEF")) {
                    tabelaDeDefinicoes.put(scanner.next(), new Simbolo((short) -1, Simbolo.ModoDeRelocabilidade.RELATIVO));
                }

                if (opname.equals("EXTR")) {
                    simbolosExternos.add(scanner.next());
                }
            }

            InstrucaoDados instrucao = Instrucoes.getInstrucao(opname);

            if (instrucao == InstrucaoDados.EQU) {
                if (tabelaDeDefinicoes.containsKey(rotulo)) {
                    tabelaDeDefinicoes.put(rotulo, new Simbolo((short) scanner.nextInt(), Simbolo.ModoDeRelocabilidade.ABSOLUTO));
                } else {
                    tabelaDeSimbolos.put(rotulo, new Simbolo((short) scanner.nextInt(), Simbolo.ModoDeRelocabilidade.ABSOLUTO));                }
            }

            contadorDePosicao += instrucao.tamanho;

            scanner.nextLine();
        }

        scanner.close();

        verificarTamanhoSimbolos();
    }

    void verificarTamanhoSimbolos() {
        Set<String> simbolos = new HashSet<>();
        simbolos.addAll(tabelaDeSimbolos.keySet());
        simbolos.addAll(tabelaDeDefinicoes.keySet());
        simbolos.addAll(simbolosExternos);
        for (String simbolo : simbolos) {
            if (simbolo.length() > 8) {
                System.out.println("Erro: o símbolo " + simbolo + " tem comprimento maior que 8.");
                System.exit(1);
            }
        }
    }

    public void segundoPasso() {
        Scanner scanner = null;
        try {
            scanner = new Scanner(modulo);
        } catch (FileNotFoundException e) {
            System.out.println("Montador: erro ao criar arquivo objeto.");
            System.exit(1);
        }

        File arquivoObjeto = new File(arquivoDeEntrada.replace(arquivoDeEntrada.substring(arquivoDeEntrada.indexOf(".")), ".OBJ"));
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

            if (rotuloOuOpname.equals("*")) {
                scanner.nextLine();
                continue;
            }
        
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
                    operandoInfo = processarOperando(scanner.next(), (short) (contadorDePosicao + 1));
                    escreverShort(outputStream, instrucao.opcode | operandoInfo.modoDeEnderecamento, false);
                    escreverShort(outputStream, operandoInfo.operando, operandoInfo.relocabilidade);
                    contadorDePosicao += 2;

                    break;
                
                case COPY:
                    OperandoInfo operando1Info = processarOperando(scanner.next(), (short) (contadorDePosicao + 1));
                    OperandoInfo operando2Info = processarOperando(scanner.next(), (short) (contadorDePosicao + 2));
                    if (operando2Info.modoDeEnderecamento == Bitmasks.ENDERECAMENTO_INDIRETO_OP1) {
                        operando2Info.modoDeEnderecamento = Bitmasks.ENDERECAMENTO_INDIRETO_OP2;
                    }

                    escreverShort(outputStream, instrucao.opcode | operando1Info.modoDeEnderecamento
                                                                 | operando2Info.modoDeEnderecamento,
                                                false);
                    escreverShort(outputStream, operando1Info.operando, operando1Info.relocabilidade);
                    escreverShort(outputStream, operando2Info.operando, operando2Info.relocabilidade);
                    contadorDePosicao += 3;
                    break;

                case STOP:
                case RET:
                    escreverShort(outputStream, instrucao.opcode, false);
                    contadorDePosicao += 1;
                    break;
                
                case CONST:
                    operandoInfo = processarOperando(scanner.next(), (short) (contadorDePosicao + 1));
                    escreverShort(outputStream, operandoInfo.operando, operandoInfo.relocabilidade);
                    contadorDePosicao += 1;
                    break;

                case SPACE:
                    escreverShort(outputStream, 0, false);
                    contadorDePosicao += 1;
                    break;

                case EXTDEF:
                    break;

                case EXTR:
                    break;

                case START:
                    break;

                case END:
                    break main_loop;
            }


            scanner.nextLine();
        }

        scanner.close();
    }

    private void escreverShort(FileOutputStream stream, int num, boolean relocabilidade) {
        short num2 = (short) num;
        try {
            stream.write((byte) (num2 >> 8));
            stream.write((byte) num2);
        } catch (IOException e) {
            System.out.println("Montador: erro em escrever arquivo objeto.");
            System.exit(1);
        }

        mapaDeRelocacao.add(relocabilidade);
    }

    class OperandoInfo {
        short modoDeEnderecamento;
        int operando;
        boolean relocabilidade;

        public OperandoInfo(short modoDeEnderecamento, int operando, boolean relocabilidade) {
            this.modoDeEnderecamento = modoDeEnderecamento;
            this.operando = operando;
            this.relocabilidade = relocabilidade;
        }
    }

    Short pegaEnderecoDeSimbolo(String simbolo) {
        if (tabelaDeSimbolos.containsKey(simbolo)) {
            return tabelaDeSimbolos.get(simbolo).endereco;
        } else if (tabelaDeDefinicoes.containsKey(simbolo)) {
            return tabelaDeDefinicoes.get(simbolo).endereco;
        } else if (simbolosExternos.contains(simbolo)) {
            return 0;
        }

        System.out.println("Rótulo não definido: " + simbolo);
        System.exit(1);
        return -1;
    }

    OperandoInfo processarOperando(String operando, short posição) {
        switch (operando.charAt(0)) {
            case 'H':   // operando imediato hexadecimal (exemplo: H'45G6')
                return new OperandoInfo(Bitmasks.ENDERECAMENTO_IMEDIATO, Integer.decode("0x" + operando.split("\'")[1]), false);
            case '@':   // operando imediato decimal
                return new OperandoInfo(Bitmasks.ENDERECAMENTO_IMEDIATO, Integer.parseInt(operando.substring(1)), false);
            case '&':   // endereçamento indireto
                if (simbolosExternos.contains(operando.substring(1))) {
                    tabelaDeUsos.put(operando.substring(1), posição);
                }
                return new OperandoInfo(Bitmasks.ENDERECAMENTO_INDIRETO_OP1, pegaEnderecoDeSimbolo(operando.substring(1)), true);
            default:    // um rótulo (endereçamento direto)
                if (simbolosExternos.contains(operando)) {
                    tabelaDeUsos.put(operando, posição);
                }
                return new OperandoInfo((short) 0, pegaEnderecoDeSimbolo(operando), true);
        }
    }

    boolean verificaTamanhoLinhas(File modulo) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(modulo);
        } catch (FileNotFoundException e) {
            System.out.println("Erro em ler o módulo de entrada: " + e.getMessage());
        }
        while (scanner.hasNextLine()) {
            String linha = scanner.nextLine();
            if (linha.length() > 80) {
                return false;
            }
        }
        return true;
    }

    void executar() {
        primeiroPasso();
        segundoPasso();

        try {
            String outputFilename = arquivoDeEntrada.replace(arquivoDeEntrada.substring(arquivoDeEntrada.indexOf(".")), ".LST");
            File arquivo = new File(outputFilename);
            PrintStream outStream = new PrintStream(arquivo);    
            Tabelas.escreveTabelaDeDefinicoes(tabelaDeDefinicoes, outStream);
            Tabelas.escreveTabelaDeUso(tabelaDeUsos, outStream);
            outStream.append("# mapaDeRelocacao: \n" + escreverMapaDeRelocacao(mapaDeRelocacao) + "\n");
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String escreverMapaDeRelocacao(ArrayList<Boolean> mapa) {
        String out = "";
        for (Boolean bit : mapa) {
            out += bit ? "1" : "0";
        }
        return out;
    }

    public static void main(String[] args) {
        Montador montador = new Montador("exemplos/definição_glob_var.txt");
        montador.executar();
        System.out.println("Tabela de símbolos: "   + montador.tabelaDeSimbolos.toString());
        System.out.println("Tabela de definições: " + montador.tabelaDeDefinicoes.toString());
        System.out.println("Tabela de uso: "        + montador.tabelaDeUsos.toString());
    }
}
