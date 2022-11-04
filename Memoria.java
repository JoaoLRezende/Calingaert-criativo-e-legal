import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Memoria {
    
    private int memTamanho = 10000;
    private short[] memoria;
    static final int STACK_LIMIT = 100;

    Memoria(int numPalavras, File arquivoObjeto) throws IOException{
        memTamanho = numPalavras;
        memoria = new short[memTamanho];
        memoria[2] = STACK_LIMIT;

        FileInputStream arquivoObjetoStream = new FileInputStream(arquivoObjeto);
        for (int i = STACK_LIMIT; arquivoObjetoStream.available() > 0; i++) {
            memoria[i] = lerShort(arquivoObjetoStream);
        }
    }

    public short[] printMemoria() {
        return memoria;
    }

    public void set(short endereco, short conteudo) { 
            memoria[endereco] = conteudo;
    }

    public short get(short endereco){
        return memoria[endereco];
    }

    private short lerShort(FileInputStream stream) throws IOException {
        short num;
        num = (byte) stream.read();
        num <<= 8;
        num += (byte) stream.read();
        return num;
    }

}

