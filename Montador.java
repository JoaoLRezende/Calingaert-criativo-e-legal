import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.HashMap;

public class Montador {
    File modulo;
    HashMap<String, Short> tabelaDeSimbolos = new HashMap<>();

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
                tabelaDeSimbolos.put(rotulo, contadorDePosicao);
            } else {
                opname = rotuloOuOpname;
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

        while (scanner.hasNext()) {
            String rotulo = null;
            String opname = null;
            String rotuloOuOpname = scanner.next();
        
            // se houver um rótulo
            if (!Instrucoes.existeOpname(rotuloOuOpname)) {
                rotulo = rotuloOuOpname;
                opname = scanner.next();
                tabelaDeSimbolos.put(rotulo, contadorDePosicao);
            } else {
                opname = rotuloOuOpname;
            }

            InstrucaoDados instrucao = Instrucoes.getInstrucao(opname);

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
                    escreverShort(outputStream, instrucao.opcode);
                    contadorDePosicao++;
                    escreverShort(outputStream, processarOperando(scanner.next()));
                    contadorDePosicao++;
                    break;
                
                case COPY:
                    escreverShort(outputStream, instrucao.opcode);
                    contadorDePosicao++;
                    break;
                    // processarOperando(scanner.next());

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

    int processarOperando(String operando) {
        switch (operando.charAt(0)) {
            case '@':   // operando imediato
                return Integer.parseInt(operando.substring(1));
                // TODO: ativar o bit de endereçamento
            case '&':   // endereçamento indireto
                return tabelaDeSimbolos.get(operando.substring(1));
            default:    // um rótulo (endereçamento direto)
                return tabelaDeSimbolos.get(operando);
        }
    }

    public static void main(String[] args) {
        Montador montador = new Montador(new File("input.txt"));
        montador.primeiroPasso();
        System.out.println(montador.tabelaDeSimbolos.toString());
        montador.segundoPasso();
    }
}
