package algorithms.mission3;

/*
    Resultado completo de Floyd-Warshall sobre un caso de la Mision 3.

    matriz[i][j]  = maximo churun acumulable en una CAMINATA (walk, con nodos
                    y aristas repetibles) desde i hasta j, o FloydWarshall.NO_ROUTE
                    si no existe ninguna caminata.
    noAcotado[i][j] = true si ese maximo es infinito (existe un ciclo de
                    ganancia positiva alcanzable desde i que a su vez puede
                    llegar a j).

    Esta clase solo guarda datos y los expone de forma segura (sin permitir
    aritmetica accidental sobre el centinela desde afuera). No importa nada
    de Swing/JavaFX: la GUI la consume para pintar la tabla N x N que exige
    la seccion 5, punto 1 del enunciado, pero el formato de celda que arma
    textoCelda() no depende de ningun componente grafico.
*/
public final class FloydWarshallResult {

    private final long[][] matriz;
    private final boolean[][] noAcotado;

    public FloydWarshallResult(long[][] matriz, boolean[][] noAcotado) {
        this.matriz = matriz;
        this.noAcotado = noAcotado;
    }

    public long[][] getMatriz() {
        return matriz;
    }

    public boolean[][] getNoAcotado() {
        return noAcotado;
    }

    public boolean esAlcanzable(int i, int j) {
        return matriz[i][j] != FloydWarshall.NO_ROUTE;
    }

    public boolean esNoAcotado(int i, int j) {
        return noAcotado[i][j];
    }

    /* Solo tiene sentido llamarla si esAlcanzable(i,j) es true: de lo contrario
       se estaria leyendo el centinela como si fuera un valor real. */
    public long getValor(int i, int j) {
        return matriz[i][j];
    }

    /*
        Texto de una celda para la tabla N x N de la GUI (seccion 5, punto 1):
        "-" si no hay ninguna caminata, "inf" si el maximo es no acotado,
        o el numero calculado en caso contrario.
    */
    public String textoCelda(int i, int j) {
        if (!esAlcanzable(i, j)) {
            return "-";
        }
        if (esNoAcotado(i, j)) {
            return "inf";
        }
        return Long.toString(matriz[i][j]);
    }
}
