
class Simbolo {
    enum ModoDeRelocabilidade { ABSOLUTO, RELATIVO };

    short endereco;
    ModoDeRelocabilidade modoDeRelocabilidade;

    public Simbolo(short endereco, Simbolo.ModoDeRelocabilidade modoDeRelocabilidade) {
        this.endereco = endereco;
        this.modoDeRelocabilidade = modoDeRelocabilidade;
    }

    public String toString() {
        return "" + endereco + " " + modoDeRelocabilidade;
    }
}