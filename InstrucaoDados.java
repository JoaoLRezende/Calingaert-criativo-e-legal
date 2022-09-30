public enum InstrucaoDados {
    ADD(2),
    BR(2),
    BRNEG(2),
    BRPOS(2),
    BRZERO(2),
    CALL(2),
    COPY(3),
    DIVIDE(2),
    LOAD(2),
    MULT(2),
    READ(2),
    RET(1),
    STOP(1),
    STORE(2),
    SUB(2),
    WRITE(2),
    CONST(1),
    END(0),
    EXTDEF(0),
    EXTR(0),
    SPACE(1),
    STACK(0),
    START(0)
    ;

    public int tamanho;

    private InstrucaoDados(int tamanho) {
        this.tamanho = tamanho;
    }
}
