import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Montador {
    public Montador(File modulo) {
        try {
            Scanner scanner = new Scanner(modulo);
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo não encontrado.");
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        Montador montador = new Montador(new File("input.txt"));
    }
}
