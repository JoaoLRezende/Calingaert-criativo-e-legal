import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class Ligador {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File("tabelas.test"));
        HashMap<String, Simbolo> tabelaDeDefinições = Tabelas.lerTabelaDeDefinicoes(scanner);
        System.out.println(tabelaDeDefinições);
        HashMap<String, Short> tabelaDeUsos = Tabelas.lerTabelaDeUsos(scanner);
        System.out.println(tabelaDeUsos);
    }
}
