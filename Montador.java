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
                    escreverShort(outputStream, Opcodes.ADD);
                    contadorDePosicao++;
                    short op1 = Short.parseShort(scanner.next());
                    escreverShort(outputStream, op1);
                    contadorDePosicao++;
                    break;
            }


            scanner.nextLine();
        }

        scanner.close();
    }

    private void escreverShort(FileOutputStream stream, short num) {
        try {
            stream.write((byte) (num >> 8));
            stream.write((byte) num);
        } catch (IOException e) {
            System.out.println("Montador: erro em escrever arquivo objeto.");
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        Montador montador = new Montador(new File("input.txt"));
        montador.primeiroPasso();
        System.out.println(montador.tabelaDeSimbolos.toString());
        montador.segundoPasso();
    }
}
