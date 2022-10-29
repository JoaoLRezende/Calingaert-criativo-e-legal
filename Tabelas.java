import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Tabelas {
    static void escreveTabelaDeDefinicoes(HashMap<String, Simbolo> tabela, PrintStream outStream) throws IOException {
        outStream.append("# tabela de definições\n");
        outStream.printf("%-10s | %-10s | %-10s\n", "simbolo", "endereco", "relocabilidade");
        for (Map.Entry<String, Simbolo> simbolo : tabela.entrySet()) {
            outStream.printf("%-10s | %-10d | %-10s\n", simbolo.getKey(),
                                                        simbolo.getValue().endereco,
                                                        simbolo.getValue().modoDeRelocabilidade);
        }
    }

    static void escreveTabelaDeUso(HashMap<String, Short> tabela, PrintStream outStream) throws IOException {
        outStream.append("# tabela de usos\n");
        outStream.printf("%-10s | %-10s\n", "simbolo", "endereco");

        for (Map.Entry<String, Short> uso : tabela.entrySet()) {
            outStream.printf("%-10s | %-10d\n", uso.getKey(),
                                                uso.getValue());
        }
    }

    static HashMap<String, Simbolo> lerTabelaDeDefinicoes(Scanner entrada) {
        HashMap<String, Simbolo> tabela = new HashMap<>();

        entrada.nextLine();
        entrada.nextLine();
        String linha = entrada.nextLine();
        while (linha.charAt(0) != '#') {
            Scanner lineScanner = new Scanner(linha);
            String nome = lineScanner.next();
            lineScanner.next(); // lê "|"
            short endereco = lineScanner.nextShort();
            lineScanner.next();
            String modoDeRelocabilidadeString = lineScanner.next();
            Simbolo.ModoDeRelocabilidade modoDeRelocabilidade;
            switch (modoDeRelocabilidadeString) {
                case "RELATIVO":
                    modoDeRelocabilidade = Simbolo.ModoDeRelocabilidade.RELATIVO;
                    break;
                case "ABSOLUTO":
                    modoDeRelocabilidade = Simbolo.ModoDeRelocabilidade.ABSOLUTO;
                    break;

                default:
                    modoDeRelocabilidade = null;
            }

            Simbolo símbolo = new Simbolo(endereco, modoDeRelocabilidade);
            tabela.put(nome, símbolo);
            
            lineScanner.close();
            linha = entrada.nextLine();
        }

        return tabela;
    }

    static HashMap<String, Short> lerTabelaDeUsos(Scanner entrada) {
        HashMap<String, Short> tabela = new HashMap<>();

        entrada.nextLine();

        String linha = entrada.nextLine();
        while(linha.charAt(0) != '#') {
            Scanner lineScanner = new Scanner(linha);
            String nome = lineScanner.next();
            lineScanner.next(); // lê "|"
            short endereco = lineScanner.nextShort();

            tabela.put(nome, endereco);

            lineScanner.close();
            linha = entrada.nextLine();
        }

        return tabela;
    }

}

