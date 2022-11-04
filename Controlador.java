import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class Controlador {
    public static void main (String[] args) throws IOException {
        // gambiarra para depurar o código no PC do João.        
        // String[] args1 = { "exemplos/chamada_add_var.txt", "exemplos/definição_add_var.txt", "exemplos/definição_glob_var.txt" };
        // args = args1;

        ArrayList<String> módulosMontados = new ArrayList<>();
        for (String arg : args) {
            ProcessadorDeMacros.executar(arg, arg + ".macroprocessado");
            Montador montador = new Montador(arg + ".macroprocessado");
            montador.executar();
            módulosMontados.add(arg.replace(arg.substring(arg.indexOf(".")), ".OBJ"));
        }
        
        new Ligador(módulosMontados.toArray(new String[0]));
        Memoria memoria = new Memoria(1000, new File(args[0].replace(args[0].substring(args[0].indexOf(".")), ".HPX")));
        Executor executor = new Executor(memoria);
        while (!executor.terminou) {
            executor.step();
        }
        System.out.println("acumulador: " + executor.getAcumulador());
    }
}
