import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
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

    // TODO
    void leTabela(HashMap<String, Simbolo> tabela, String arquivoDeSaida) {
        File arquivo = new File(arquivoDeSaida);
        Scanner scanner = null;
        try {
            scanner = new Scanner(arquivo);
        } catch (FileNotFoundException e) {
            System.out.println("Tabelas: escreveTabela: erro ao criar arquivo de saída: " + e.getMessage());
            System.exit(1);
        }

        
    }
}
