import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class ProcessadorDeMacros {

    static class Macro {
        String[] parâmetros;
        public Macro(String[] parâmetros) {
            this.parâmetros = parâmetros;
            corpo = new String();
        }

        String corpo;
    }
    
    static void executar(File moduloEntrada, File moduloSaida) throws FileNotFoundException {
        Scanner fileScanner = new Scanner(moduloEntrada);

        PrintStream outStream = new PrintStream(moduloSaida);
        
        boolean modoDeDefinição = false;
        boolean modoDeExpansão = false;
        HashMap<String, Macro> tabelaDeMacros = new HashMap<>();
        Macro macroSendoDefinida = null;
        while (fileScanner.hasNext()) {
            String linha = fileScanner.nextLine();
            Scanner lineScanner = new Scanner(linha);

            String opcode = lineScanner.next();
            // se opcode for na verdade um rótulo
            if (!opcode.equals("MACRO") && !opcode.equals("MEND") && !Instrucoes.existeOpname(opcode) && !tabelaDeMacros.containsKey(opcode)) {
                opcode = lineScanner.next();
            }

            if (modoDeDefinição) {
                if (opcode.equals("MEND")) {
                    modoDeDefinição = false;
                } else {
                    macroSendoDefinida.corpo += substituiReferenciasAParâmetros(linha, macroSendoDefinida.parâmetros) + "\n";
                }

            } else {
                if (opcode.equals("MACRO")) {
                    modoDeDefinição = true;
                    String protótipo = fileScanner.nextLine();
                    String[] tokensPrototipo = protótipo.trim().split("\\s+");

                    String nomeDaMacro = tokensPrototipo[0];
                    String[] parâmetros = Arrays.copyOfRange(tokensPrototipo, 1, Math.max(2, tokensPrototipo.length));

                    macroSendoDefinida = new Macro(parâmetros);
                    tabelaDeMacros.put(nomeDaMacro, macroSendoDefinida);
                }
            }
        }
        System.out.println(tabelaDeMacros);
    }

    static String substituiReferenciasAParâmetros(String linha, String[] parâmetros) {
        ArrayList<String> listaParâmetros = new ArrayList<String>(Arrays.asList(parâmetros));

        String novaLinha = "";
        Scanner scanner = new Scanner(linha);
        while (scanner.hasNext()) {
            String token = scanner.next();

            int índiceParâmetro = listaParâmetros.indexOf(token);
            if (índiceParâmetro != -1) {
                novaLinha += "#" + (índiceParâmetro + 1) + " ";
            } else {
                novaLinha += token + " ";
            }

        }
        scanner.close();
        return novaLinha.trim();
    }

    public static void main(String[] args) throws FileNotFoundException  {
        ProcessadorDeMacros.executar(new File("input.txt"), new File("MASMAPRG.ASM"));
    }
}
