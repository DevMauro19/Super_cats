package algorithms.mission3;

/*
    Resultado de Bellman-Ford desde un origen S fijo para un caso de la Mision 3.

    distancias[v] = maximo churun acumulable en una caminata de S a v, o
                    FloydWarshall.NO_ROUTE si v no es alcanzable desde S.
    noAcotado[v]  = true si ese maximo es infinito: v pertenece a un ciclo de
                    ganancia positiva alcanzable desde S, o es alcanzable desde
                    un nodo que si pertenece a uno.

    Comparte el mismo centinela que FloydWarshallResult (FloydWarshall.NO_ROUTE)
    para que Mission3Solver pueda cruzar ambos resultados sin traducir valores.
*/
public final class BellmanFordResult {

    private final long[] distancias;
    private final boolean[] noAcotado;
    private final int origen;

    public BellmanFordResult(long[] distancias, boolean[] noAcotado, int origen) {
        this.distancias = distancias;
        this.noAcotado = noAcotado;
        this.origen = origen;
    }

    public long[] getDistancias() {
        return distancias;
    }

    public boolean[] getNoAcotado() {
        return noAcotado;
    }

    public int getOrigen() {
        return origen;
    }

    public boolean esAlcanzable(int nodo) {
        return distancias[nodo] != FloydWarshall.NO_ROUTE;
    }

    public boolean esNoAcotado(int nodo) {
        return noAcotado[nodo];
    }

    /* Solo tiene sentido si esAlcanzable(nodo) es true. */
    public long getDistancia(int nodo) {
        return distancias[nodo];
    }
}
