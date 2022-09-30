public enum InstrucaoDados {
    ADD((short) 2, 2);

    public short opcode;
    public int tamanho;

    private InstrucaoDados(short opcode, int tamanho) {
        this.opcode = opcode;
        this.tamanho = tamanho;
    }
}
