public enum InstrucaoDados {
    ADD(2, 2),
    BR(0, 2),
    BRNEG(5, 2),
    BRPOS(1, 2),
    BRZERO(4, 2),
    CALL(15, 2),
    COPY(13, 3),
    DIVIDE(10, 2),
    LOAD(3, 2),
    MULT(14, 2),
    READ(12, 2),
    RET(16, 1),
    STOP(11, 1),
    STORE(7, 2),
    SUB(6, 2),
    WRITE(8, 2),
    CONST(-1, 1),
    END(-1, 0),
    EXTDEF(1, 0),
    EXTR(-1, 0),
    SPACE(-1, 1),
    STACK(-1, 0),
    START(-1, 0)
    ;

    public int opcode;
    public int tamanho;

    private InstrucaoDados(int opcode, int tamanho) {
        this.opcode = opcode;
        this.tamanho = tamanho;
    }
}
