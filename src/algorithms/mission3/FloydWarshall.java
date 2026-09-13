package algorithms.mission3;

import Exceptions.EFueraRango;
import model.Graph;
import model.WeightedEdge;

import java.util.Arrays;

/*
    Mision 3: maximo churun acumulable entre TODOS los pares de nodos, resuelto
    con Floyd-Warshall adaptado a MAXIMIZACION en lugar de minimizacion.

    La ruta puede repetir nodos y aristas (es una caminata, no un camino simple):
    por eso el problema es tratable con programacion dinamica en lugar de ser
    NP-duro. d[i][j] se interpreta como "el mejor churun que puede acumularse
    yendo de i a j, permitiendo pasar cuantas veces se quiera por cualquier nodo
    intermedio ya considerado".

    Se usa long para los acumulados (seccion 2.1) y un SENTINELA (NO_ROUTE) para
    "no existe caminata". Nunca se hace aritmetica directa sobre el centinela:
    toda suma esta protegida por una comprobacion "!= NO_ROUTE" antes de sumar,
    tal como exige la seccion 2.1. NO_ROUTE se fija en Long.MIN_VALUE / 4 (no en
    Long.MIN_VALUE) para que, aunque alguna suma se colara por error, el
    resultado siga siendo un numero muy negativo y no se desborde (overflow)
    hacia un positivo falso.

    Complejidad temporal: O(N^3) para el triple bucle clasico, mas otro O(N^3)
    para la pasada que marca los pares no acotados (ver mas abajo) -> O(N^3)
    en total. Con N <= 100 (limite del enunciado) son unas 10^6 operaciones,
    trivial. Complejidad espacial: O(N^2) para la matriz de resultados y otra
    O(N^2) para las marcas de no acotado.

    Por que Floyd-Warshall y no solo Bellman-Ford (Mision 2 usa Dijkstra en vez
    de esto porque alli los pesos son no negativos): aqui se necesitan TODOS los
    pares, no solo los alcanzables desde un origen fijo, y Floyd-Warshall calcula
    los N^2 pares en el mismo O(N^3) en el que Bellman-Ford calcularia un unico
    origen. Bellman-Ford se usa ademas (por separado, en BellmanFord.java) para
    validar por un camino totalmente distinto el resultado de este algoritmo.

    Esta clase no importa nada de Swing ni de JavaFX: se puede ejecutar completa
    desde un test, como exige la seccion 7.2 del enunciado.
*/
public final class FloydWarshall {

    /*
        Centinela de "no existe ninguna caminata entre estos dos nodos".
        Se comparte con BellmanFord.java para que ambos algoritmos usen
        exactamente el mismo valor y el cruce de resultados (Mission3Solver)
        pueda comparar sin ambiguedad.
    */
    public static final long NO_ROUTE = Long.MIN_VALUE / 4;

    private FloydWarshall() {
    }

    public static FloydWarshallResult resolver(Graph grafo) throws EFueraRango {

        int n = grafo.getContadorNodos();
        long[][] d = new long[n][n];

        for (long[] fila : d) {
            Arrays.fill(fila, NO_ROUTE);
        }

        // Quedarse quieto en un nodo siempre es una caminata valida de costo 0.
        for (int i = 0; i < n; i++) {
            d[i][i] = 0;
        }

        /*
            Se inicializa con la lista PLANA de aristas (no con getNeightbors)
            porque aqui puede haber pasajes repetidos entre el mismo par
            ordenado (seccion 5: "treat each one as a separate edge"), y solo
            interesa quedarse con el de mayor churun para arrancar la tabla.
            "Initialize d[i][i] = 0, keep the maximum when an ordered pair
            appears more than once": por eso se compara siempre contra el
            valor ya guardado, incluso cuando el par es (i, i) y ese valor
            ya viene inicializado en 0 por el bucle de arriba.
        */
        for (WeightedEdge arista : grafo.getEdges()) {
            int a = arista.getFrom();
            int b = arista.getTo();
            long w = arista.getWeight();
            if (d[a][b] == NO_ROUTE || w > d[a][b]) {
                d[a][b] = w;
            }
        }

        /*
            Triple bucle clasico de Floyd-Warshall, invertido para maximizar:
            para cada nodo intermedio k, se pregunta si pasar por k mejora la
            mejor caminata conocida entre i y j.

            La guarda "d[i][k] == NO_ROUTE -> continue" (y su equivalente para
            d[k][j]) es la que impide sumar sobre el centinela: si no hay
            caminata hasta k, o desde k no se puede seguir, k no sirve como
            intermedio y no se toca d[i][j].
        */
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                if (d[i][k] == NO_ROUTE) {
                    continue;
                }
                for (int j = 0; j < n; j++) {
                    if (d[k][j] == NO_ROUTE) {
                        continue;
                    }
                    long candidato = d[i][k] + d[k][j];
                    if (d[i][j] == NO_ROUTE || candidato > d[i][j]) {
                        d[i][j] = candidato;
                    }
                }
            }
        }

        /*
            Pasada extra para marcar los pares NO ACOTADOS (seccion 5, punto 1):

            (i, j) es no acotado si y solo si existe un nodo k tal que:
                - hay caminata finita de i a k       (d[i][k] != NO_ROUTE)
                - k pertenece a un ciclo de ganancia positiva (d[k][k] > 0)
                - hay caminata finita de k a j       (d[k][j] != NO_ROUTE)

            Sin esta pasada, d[i][j] quedaria con un numero grande pero finito
            (el mejor valor que el triple bucle alcanzo a acumular en sus N
            iteraciones), en vez de reflejar que en realidad se puede ganar
            churun sin limite dando vueltas en ese ciclo.

            No hace falta filtrar aqui los pares con d[i][j] == NO_ROUTE: si
            existiera un k valido para ese (i, j), el propio triple bucle ya
            habria usado k como intermedio y d[i][j] no seria NO_ROUTE.
        */
        boolean[][] noAcotado = new boolean[n][n];
        for (int i = 0; i < n; i++) {
            for (int k = 0; k < n; k++) {
                if (d[i][k] == NO_ROUTE || d[k][k] <= 0) {
                    continue;
                }
                for (int j = 0; j < n; j++) {
                    if (d[k][j] != NO_ROUTE) {
                        noAcotado[i][j] = true;
                    }
                }
            }
        }

        return new FloydWarshallResult(d, noAcotado);
    }
}
