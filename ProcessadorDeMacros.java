import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;

public class ProcessadorDeMacros {

    static class Macro {
        String[] parâmetros;

        public Macro(String[] parâmetros) {
            this.parâmetros = parâmetros;
            corpo = new String();
        }

        String corpo;
    }

    static class Parâmetro {
        public Parâmetro(String nome, int nível, int posição) {
            this.nome = nome;
            this.nível = nível;
            this.posição = posição;
        }

        String nome;
        int nível;
        int posição;
    }

    static void executar(File moduloEntrada, File moduloSaida) throws FileNotFoundException {
        Scanner fileScanner = new Scanner(moduloEntrada);

        PrintStream outStream = new PrintStream(moduloSaida);

        int nivelDeDefinição = 0;
        HashMap<String, Macro> tabelaDeMacros = new HashMap<>();
        Macro macroSendoDefinida = null;
        int númeroDeExpansões = 0;
        Stack<Parâmetro> pilhaDeParâmetros = new Stack<>();
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
                for (int i = 0; i < parâmetros.length; i++) {
                    pilhaDeParâmetros.add(new Parâmetro(parâmetros[i], nivelDeDefinição, i));
                }

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
                    while (pilhaDeParâmetros.size() > 0 && pilhaDeParâmetros.peek().nível == nivelDeDefinição) {
                        pilhaDeParâmetros.pop();
                    }
                    nivelDeDefinição -= 1;
                } else if (nivelDeDefinição > 1) {
                    macroSendoDefinida.corpo += substituiReferenciasAParâmetros(linha, macroSendoDefinida.parâmetros, pilhaDeParâmetros)
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
            lineScanner.close();
        }
        System.out.println(tabelaDeMacros);
        fileScanner.close();
    }

    static void expandirMacro(Macro macro, String[] argumentos, PrintStream outStream, int contadorDeExpansões) {
        Scanner macroScanner = new Scanner(macro.corpo);
        while (macroScanner.hasNext()) {
            String linha = macroScanner.nextLine();
            Scanner lineScanner = new Scanner(linha);
            while (lineScanner.hasNext()) {
                String token = lineScanner.next();
                if (token.charAt(0) == '#') {
                    Scanner tokenScanner = new Scanner(token.substring(2, token.length() - 1));
                    tokenScanner.useDelimiter(",");
                    int nívelDeDefinição = tokenScanner.nextInt();
                    int índiceArgumento = tokenScanner.nextInt();
                    tokenScanner.close();
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

    static String substituiReferenciasAParâmetros(String linha, String[] parâmetros, Stack<Parâmetro> pilhaDeParâmetros) {
        String novaLinha = "";
        Scanner scanner = new Scanner(linha);
        while (scanner.hasNext()) {
            String token = scanner.next();
            
            Parâmetro parâmetro = null;
            for (int i = pilhaDeParâmetros.size() - 1; i >= 0; i--) {
                if (pilhaDeParâmetros.get(i).nome.equals(token)) {
                    parâmetro = pilhaDeParâmetros.get(i);
                    break;
                }
            }

            if (parâmetro != null) {
                novaLinha += "#(" + parâmetro.nível + "," + parâmetro.posição + ") ";
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
