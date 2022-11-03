import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Ligador {

    short contadorDePosição = Executor.STACK_LIMIT;
    HashMap<String, Simbolo> tabelaDeSímbolosGlobal = new HashMap<>();

    public Ligador(String[] arquivosDeEntrada) throws IOException {
        short[] endereçosBaseDosMódulos = new short[arquivosDeEntrada.length];

        // primeiro passo: preencher tabela de símbolos globais
        int i = 0;
        for (String nomeArquivoObjeto : arquivosDeEntrada) {
            String nomeArquivoDeListagem = nomeArquivoObjeto
                    .replace(nomeArquivoObjeto.substring(nomeArquivoObjeto.indexOf(".")), ".LST");

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
            endereçosBaseDosMódulos[i++] = contadorDePosição;
            contadorDePosição += Files.size(Path.of(nomeArquivoObjeto)) / 2;
        }

        // segundo passo: concatenar módulos, resolvendo referências e relocando
        // endereços.
        File arquivoDeSaída = new File(arquivosDeEntrada[0]
                .replace(arquivosDeEntrada[0].substring(arquivosDeEntrada[0].indexOf(".")), ".HPX"));
        FileOutputStream streamSaída = new FileOutputStream(arquivoDeSaída);
        int índiceDoMódulo = 0;
        for (String nomeArquivoObjeto : arquivosDeEntrada) {
            String nomeArquivoDeListagem = nomeArquivoObjeto
                    .replace(nomeArquivoObjeto.substring(nomeArquivoObjeto.indexOf(".")), ".LST");

            File arquivoObjeto = new File(nomeArquivoObjeto);
            File arquivoDeListagem = new File(nomeArquivoDeListagem);

            // ler arquivo de listagem
            Scanner scannerArquivoDeListagem = new Scanner(arquivoDeListagem);
            Tabelas.lerTabelaDeDefinicoes(scannerArquivoDeListagem);
            HashMap<String, Short> tabelaDeUsos = Tabelas.lerTabelaDeUsos(scannerArquivoDeListagem);

            HashMap<Short, String> mapaDeUsos = new HashMap<>();
            tabelaDeUsos.forEach((key, value) -> mapaDeUsos.put(value, key));

            ArrayList<Boolean> mapaDeRelocação = lerMapaDeRelocacao(scannerArquivoDeListagem.next());

            FileInputStream arquivoObjetoStream = new FileInputStream(arquivoObjeto);
            short palavrasJáLidasDesteMódulo = 0;
            while (arquivoObjetoStream.available() > 0) {
                short palavra = lerShort(arquivoObjetoStream);
                if (mapaDeUsos.containsKey(palavrasJáLidasDesteMódulo)) {
                    escreverShort(streamSaída,
                            tabelaDeSímbolosGlobal.get(mapaDeUsos.get(palavrasJáLidasDesteMódulo)).endereco);
                } else if (mapaDeRelocação.get(palavrasJáLidasDesteMódulo)) {
                    escreverShort(streamSaída, palavra + endereçosBaseDosMódulos[índiceDoMódulo]);
                } else {
                    escreverShort(streamSaída, palavra);
                }

                palavrasJáLidasDesteMódulo += 1;
            }
            índiceDoMódulo += 1;
        }
    }

    private short lerShort(FileInputStream stream) throws IOException {
        short num;
        num = (byte) stream.read();
        num <<= 8;
        num += (byte) stream.read();
        return num;
    }

    private void escreverShort(FileOutputStream stream, int num) {
        short num2 = (short) num;
        try {
            stream.write((byte) (num2 >> 8));
            stream.write((byte) num2);
        } catch (IOException e) {
            System.out.println("Ligador: erro em escrever arquivo objeto.");
            System.exit(1);
        }
    }

    ArrayList<Boolean> lerMapaDeRelocacao(String mapaString) {
        ArrayList<Boolean> mapa = new ArrayList<>();

        for (char bit : mapaString.toCharArray()) {
            mapa.add((bit == '0') ? false : true);
        }
        return mapa;
    }

    public static void main(String[] args) throws IOException {
        String[] arquivosDeEntrada = { "exemplos/chamada_add_var.OBJ",
                "exemplos/definição_add_var.OBJ",
                "exemplos/definição_glob_var.OBJ" };
        new Ligador(arquivosDeEntrada);
    }
}
