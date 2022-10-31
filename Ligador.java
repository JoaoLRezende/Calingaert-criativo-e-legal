import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Scanner;

public class Ligador {
    
    short contadorDePosição = 0;
    HashMap<String, Simbolo> tabelaDeSímbolosGlobal = new HashMap<>();

    public Ligador(String[] ArquivosDeEntrada) throws IOException {
        for (String arg : ArquivosDeEntrada) {
            String nomeArquivoObjeto = arg;
            String nomeArquivoDeListagem = arg.replace(arg.substring(arg.indexOf(".")), ".LST");
            
            File arquivoObjeto = new File(nomeArquivoObjeto);
            File arquivoDeListagem = new File(nomeArquivoDeListagem);

            // ler arquivo de listagem
            Scanner scannerArquivoDeListagem = new Scanner(arquivoDeListagem);
            HashMap<String, Simbolo> tabelaDeDefinições = Tabelas.lerTabelaDeDefinicoes(scannerArquivoDeListagem);
            tabelaDeSímbolosGlobal.putAll(tabelaDeDefinições);
            for (var símbolo : tabelaDeDefinições.entrySet()) {
                short endereço = (short) (símbolo.getValue().endereco + contadorDePosição);

                tabelaDeSímbolosGlobal.put(símbolo.getKey(), new Simbolo(endereço,
                                                                         símbolo.getValue().modoDeRelocabilidade));

            }
            contadorDePosição += Files.size(Path.of(nomeArquivoObjeto)) / 2;
                                                                        
            System.out.println(tabelaDeDefinições);
            HashMap<String, Short> tabelaDeUsos = Tabelas.lerTabelaDeUsos(scannerArquivoDeListagem);
            System.out.println(tabelaDeUsos);
        }

        System.out.println(tabelaDeSímbolosGlobal);
    }

    public static void main(String[] args) throws IOException {
        String[] arquivosDeEntrada = { "input.OBJ", "exemplos/exemplo_simples_2.OBJ" };
        new Ligador(arquivosDeEntrada);
    }
}
