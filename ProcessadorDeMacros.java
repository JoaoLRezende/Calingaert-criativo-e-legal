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

        int nivelDeDefinição = 0;
        HashMap<String, Macro> tabelaDeMacros = new HashMap<>();
        Macro macroSendoDefinida = null;
        int númeroDeExpansões = 0;
        while (fileScanner.hasNext()) {
            String linha = fileScanner.nextLine();
            if (linha.trim().equals("*")) {
                continue;
            }

            Scanner lineScanner = new Scanner(linha);

            String opcode = lineScanner.next();
            // se opcode for na verdade um rótulo
            if (!opcode.equals("MACRO") && !opcode.equals("MEND") && !Instrucoes.existeOpname(opcode)
                    && !tabelaDeMacros.containsKey(opcode)) {
                opcode = lineScanner.next();
            }

            if (opcode.equals("MACRO")) {
                nivelDeDefinição += 1;
                String protótipo = fileScanner.nextLine();
                String[] tokensPrototipo = protótipo.trim().split("\\s+");

                String nomeDaMacro = tokensPrototipo[0];
                String[] parâmetros = Arrays.copyOfRange(tokensPrototipo, 1, Math.max(2, tokensPrototipo.length));

                if (nivelDeDefinição == 1) {
                    macroSendoDefinida = new Macro(parâmetros);
                    tabelaDeMacros.put(nomeDaMacro, macroSendoDefinida);
                } else {
                    macroSendoDefinida.corpo += "MACRO\n";
                    macroSendoDefinida.corpo += protótipo + "\n";
                }
            }

            if (nivelDeDefinição > 0) {
                if (opcode.equals("MEND")) {
                    nivelDeDefinição -= 1;
                } else if (nivelDeDefinição > 1) {
                    macroSendoDefinida.corpo += substituiReferenciasAParâmetros(linha, macroSendoDefinida.parâmetros)
                            + "\n";
                }

                if (opcode.equals("MEND") && nivelDeDefinição > 0) {
                    macroSendoDefinida.corpo += "MEND\n";
                }

            } else { // se não estamos em modo de definição
                if (tabelaDeMacros.containsKey(opcode)) { // ser for uma chamada de macro
                    Macro macroSendoExpandida = tabelaDeMacros.get(opcode);
                    String[] tokensChamada = linha.trim().split("\\s+");
                    String[] argumentosDaMacroSendoExpandida = Arrays.copyOfRange(tokensChamada, 1,
                            Math.max(2, tokensChamada.length));

                    expandirMacro(macroSendoExpandida, argumentosDaMacroSendoExpandida, outStream,
                            númeroDeExpansões++);
                } else { // se não é definição nem chamada de macro
                    outStream.append(linha.trim() + "\n");
                }
            }

        }
        System.out.println(tabelaDeMacros);
    }

    static void expandirMacro(Macro macro, String[] argumentos, PrintStream outStream, int contadorDeExpansões) {
        Scanner macroScanner = new Scanner(macro.corpo);
        while (macroScanner.hasNext()) {
            String linha = macroScanner.nextLine();
            Scanner lineScanner = new Scanner(linha);

            while (lineScanner.hasNext()) {
                String token = lineScanner.next();
                if (token.charAt(0) == '#') {
                    int índiceArgumento = Integer.parseInt(token.substring(1));
                    outStream.append(argumentos[índiceArgumento - 1] + (lineScanner.hasNext() ? " " : ""));
                } else {
                    token = token.replace(".SER", Integer.valueOf(contadorDeExpansões).toString());
                    outStream.append(token + (lineScanner.hasNext() ? " " : ""));
                }
            }
            outStream.append("\n");
            lineScanner.close();
        }
        macroScanner.close();
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
        if (novaLinha.trim().equals("MACRO")) {
            return "*";
        }
        return novaLinha.trim();
    }

    public static void main(String[] args) throws FileNotFoundException {
        ProcessadorDeMacros.executar(new File("input.txt"), new File("MASMAPRG.ASM"));
    }
}
