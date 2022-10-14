import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class ProcessadorDeMacros {

    static class Macro {
        String[] parâmetros;
        public Macro(String[] parâmetros) {
            this.parâmetros = parâmetros;
        }

        String corpo;
    }
    
    static void executar(File moduloEntrada, File moduloSaida) throws FileNotFoundException {
        Scanner fileScanner = new Scanner(moduloEntrada);

        PrintStream outStream = new PrintStream(moduloSaida);
        
        boolean modoDeDefinição = false;
        boolean modoDeExpansão = false;
        HashMap<String, Macro> tabelaDeMacros = new HashMap<>();
        while (fileScanner.hasNext()) {
            String linha = fileScanner.nextLine();
            Scanner lineScanner = new Scanner(linha);

            String opcode = lineScanner.next();
            // se opcode for na verdade um rótulo
            if (!opcode.equals("MACRO") && !Instrucoes.existeOpname(opcode) && !tabelaDeMacros.containsKey(opcode)) {
                opcode = lineScanner.next();
            }

            if (modoDeDefinição) {
                if (opcode.equals("MEND")) {
                    modoDeDefinição = false;
                }

            } else {
                if (opcode.equals("MACRO")) {
                    modoDeDefinição = true;
                    String protótipo = fileScanner.nextLine();
                    String[] tokensPrototipo = protótipo.trim().split("\\s+");

                    String nomeDaMacro = tokensPrototipo[0];
                    String[] parâmetros = Arrays.copyOfRange(tokensPrototipo, 1, Math.max(2, tokensPrototipo.length -1 -1));

                    tabelaDeMacros.put(nomeDaMacro, new Macro(parâmetros));
                }
            }
        }
        System.out.println(tabelaDeMacros);
    }

    public static void main(String[] args) throws FileNotFoundException  {
        ProcessadorDeMacros.executar(new File("input.txt"), new File("MASMAPRG.ASM"));
    }
}
