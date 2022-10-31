import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class Ligador {
    HashMap<String, Simbolo> tabelaDeSímbolosGlobal = new HashMap<>();

    public Ligador(String[] ArquivosDeEntrada) throws FileNotFoundException {
        for (String arg : ArquivosDeEntrada) {
            String nomeArquivoObjeto = arg;
            String nomeArquivoDeListagem = arg.replace(arg.substring(arg.indexOf(".")), ".LST");
            
            File arquivoObjeto = new File(nomeArquivoObjeto);
            File arquivoDeListagem = new File(nomeArquivoDeListagem);

            // ler arquivo de listagem
            Scanner scannerArquivoDeListagem = new Scanner(arquivoDeListagem);
            HashMap<String, Simbolo> tabelaDeDefinições = Tabelas.lerTabelaDeDefinicoes(scannerArquivoDeListagem);
            tabelaDeSímbolosGlobal.putAll(tabelaDeDefinições);

            System.out.println(tabelaDeDefinições);
            HashMap<String, Short> tabelaDeUsos = Tabelas.lerTabelaDeUsos(scannerArquivoDeListagem);
            System.out.println(tabelaDeUsos);
        }

        System.out.println(tabelaDeSímbolosGlobal);
    }

    public static void main(String[] args) throws FileNotFoundException {
        String[] arquivosDeEntrada = { "input.OBJ", "exemplos/exemplo_simples_2.OBJ" };
        new Ligador(arquivosDeEntrada);
    }
}
