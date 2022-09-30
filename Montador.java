import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.HashMap;

public class Montador {
    Scanner scanner;
    HashMap<String, Short> tabelaDeSimbolos = new HashMap<>();

    public Montador(File modulo) {
        try {
            scanner = new Scanner(modulo);
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo não encontrado.");
            System.exit(1);
        }
    }

    public void primeiroPasso() {
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
    }

    public static void main(String[] args) {
        Montador montador = new Montador(new File("input.txt"));
        montador.primeiroPasso();
        System.out.println(montador.tabelaDeSimbolos.toString());
    }
}
