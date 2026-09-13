package algorithms.mission3;

/*
    Resultado final de un caso de la Mision 3, ya con la precedencia del
    enunciado aplicada (seccion 5):
        1) BLOQUEADO -> "Limon blocked the way"
        2) INFINITO  -> "Infinite churun!"
        3) VALOR     -> el numero (puede ser negativo)

    Ademas guarda si Floyd-Warshall y Bellman-Ford COINCIDIERON para este
    caso. Eso es lo que la GUI debe mostrar como advertencia si alguna vez
    difieren (seccion 5, punto 2), y lo que el checklist final pide poder
    ejercitar al menos una vez en un test.
*/
public final class Mission3Result {

    public enum Estado {BLOQUEADO, INFINITO, VALOR}

    private final Estado estado;
    private final long valor;
    private final boolean mismatch;
    private final String detalleMismatch;

    private Mission3Result(Estado estado, long valor, boolean mismatch, String detalleMismatch) {
        this.estado = estado;
        this.valor = valor;
        this.mismatch = mismatch;
        this.detalleMismatch = detalleMismatch;
    }

    public static Mission3Result bloqueado(boolean mismatch, String detalle) {
        return new Mission3Result(Estado.BLOQUEADO, 0, mismatch, detalle);
    }

    public static Mission3Result infinito(boolean mismatch, String detalle) {
        return new Mission3Result(Estado.INFINITO, 0, mismatch, detalle);
    }

    public static Mission3Result valor(long valor, boolean mismatch, String detalle) {
        return new Mission3Result(Estado.VALOR, valor, mismatch, detalle);
    }

    public Estado getEstado() {
        return estado;
    }

    /* Solo tiene sentido si getEstado() == VALOR. */
    public long getValor() {
        return valor;
    }

    public boolean hayMismatch() {
        return mismatch;
    }

    /* null si hayMismatch() es false. */
    public String getDetalleMismatch() {
        return detalleMismatch;
    }
}
