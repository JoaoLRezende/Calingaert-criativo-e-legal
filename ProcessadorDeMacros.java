import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
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
        boolean modoDeExpansão = false;
        String[] argumentosDaMacroSendoExpandida = null;
        // Scanner da macro que está sendo expandida, se estamos no modo de expansão.
        Scanner macroScanner = null;
        HashMap<String, Macro> tabelaDeMacros = new HashMap<>();
        Macro macroSendoDefinida = null;
        int númeroDeExpansões = 0;
        Stack<Parâmetro> pilhaDeParâmetros = new Stack<>();
        String linha = fileScanner.nextLine();
        while (!linha.equals("")) {
            Scanner lineScanner = new Scanner(linha);

            String opcode = lineScanner.next();
            // se opcode for na verdade um rótulo
            if (!opcode.equals("MACRO") && !opcode.equals("MEND") && !Instrucoes.existeOpname(opcode)
                    && !tabelaDeMacros.containsKey(opcode)) {
                opcode = lineScanner.next();
            }

            // pseudo-laço que é executado somente uma vez;
            // usado para que que possamos executar "break" para pular para o fim do bloco.
            while (true) {
                if (opcode.equals("MACRO")) {
                    nivelDeDefinição += 1;
                    // Aqui, nós já lemos e processamos também o protótipo que aparece na linha
                    // seguinte. (Isso diverge do algoritmo do livro do Calingaert, que sempre lê
                    // exatamente uma linha por iteração deste laço.)
                    // TODO: nós devemos ler de fileScanner apenas se não estamos em modo de
                    // expansão. Se estivermos, devemos ler de macroScanner. (Acho que um operador
                    // ternário serve.)
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
                    break;
                }

                // ser for uma chamada de macro
                if (tabelaDeMacros.containsKey(opcode)) {
                    modoDeExpansão = true;
                    númeroDeExpansões += 1;
                    Macro macroSendoExpandida = tabelaDeMacros.get(opcode);
                    macroScanner = new Scanner(macroSendoExpandida.corpo);
                    String[] tokensChamada = linha.trim().split("\\s+");
                    argumentosDaMacroSendoExpandida = Arrays.copyOfRange(tokensChamada, 1,
                            Math.max(2, tokensChamada.length));
                }

                if (opcode.equals("MEND")) {
                    argumentosDaMacroSendoExpandida = null;
                    if (nivelDeDefinição == 0) {
                        modoDeExpansão = false;
                    } else if (!modoDeExpansão) {
                        // desempilhar parâmetros do nível de definição do qual estamos saindo
                        while (pilhaDeParâmetros.size() > 0 && pilhaDeParâmetros.peek().nível == nivelDeDefinição) {
                            pilhaDeParâmetros.pop();
                        }
                        nivelDeDefinição -= 1;
                        macroSendoDefinida.corpo += "MEND\n";
                    }
                    break;
                }

                if (modoDeExpansão) {
                    linha = substituirReferênciasAArgumentos(linha, argumentosDaMacroSendoExpandida);
                    linha = cocatenarContadorARótulos(linha, númeroDeExpansões);
                }
                if (!modoDeExpansão && nivelDeDefinição > 0) {
                    linha = substituiReferenciasAParâmetros(linha,
                            macroSendoDefinida.parâmetros,
                            pilhaDeParâmetros);
                }
                if (nivelDeDefinição == 0) {
                    outStream.append(linha.trim() + "\n");
                } else {
                    macroSendoDefinida.corpo += linha.trim() + "\n";
                }
                break;
            }

            lineScanner.close();

            // ler próxima linha
            if (modoDeExpansão) {
                if (macroScanner.hasNext()) {
                    linha = macroScanner.nextLine();
                } else {
                    modoDeExpansão = false;
                }
            }
            if (!modoDeExpansão) {
                linha = fileScanner.nextLine();
                while (linha.trim().equals("*")) {
                    linha = fileScanner.nextLine();
                }

            }
        }

        System.out.println(tabelaDeMacros);
        fileScanner.close();
        outStream.close();

    }

    static String substituirReferênciasAArgumentos(String linha, String[] argumentos) {
        String linhaNova = "";
        Scanner lineScanner = new Scanner(linha);
        while (lineScanner.hasNext()) {
            String token = lineScanner.next();
            if (token.charAt(0) == '#') {
                Scanner tokenScanner = new Scanner(token.substring(2, token.length() - 1));
                tokenScanner.useDelimiter(",");
                int nívelDeDefinição = tokenScanner.nextInt();
                int índiceArgumento = tokenScanner.nextInt();
                tokenScanner.close();

                if (nívelDeDefinição == 1) {
                    linhaNova += argumentos[índiceArgumento - 1] + (lineScanner.hasNext() ? " " : "");
                } else {
                    linhaNova += "#(" + (nívelDeDefinição - 1) + "," + índiceArgumento + ")"
                            + (lineScanner.hasNext() ? " " : "");
                }
            }
        }
        lineScanner.close();
        return linhaNova;
    }

    static String cocatenarContadorARótulos(String linha, int contadorDeExpansões) {
        String linhaNova = "";
        Scanner lineScanner = new Scanner(linha);
        while (lineScanner.hasNext()) {
            String token = lineScanner.next();
            token = token.replace(".SER", Integer.valueOf(contadorDeExpansões).toString());
            linhaNova += token + (lineScanner.hasNext() ? " " : "");
        }
        lineScanner.close();
        return linhaNova;
    }

    static String substituiReferenciasAParâmetros(String linha, String[] parâmetros,
            Stack<Parâmetro> pilhaDeParâmetros) {
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
