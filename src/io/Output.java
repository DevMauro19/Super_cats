package io;

import algorithms.mission1.PathResult;
import algorithms.mission2.DijkstraResult;
import algorithms.mission3.Mission3Result;
import algorithms.mission4.Kruskal;

/*
    Convierte los resultados de un algoritmo en la linea de texto exacta que
    pide el enunciado (seccion 2.2). No calcula nada, no valida nada: solo
    formatea. Por eso no lanza ninguna excepcion.

    Cada mision agrega su propio metodo estatico, igual que Input.
*/
public final class Output {

    private Output() {
    }

    /*
        MISION 1 (BFS/DFS).

        Formato exigido:
            Case #k: BFS <b> DFS <d>
        o, si Nina no es alcanzable:
            Case #k: Nina is unreachable

        BFS y DFS siempre coinciden en si el destino es alcanzable o no
        (ambos recorren el mismo componente conexo de la grilla), asi que
        basta con revisar uno de los dos resultados.
    */
    public static String formatearMision1(int numeroCaso, PathResult bfs, PathResult dfs) {
        if (!bfs.isReachable()) {
            return "Case #" + numeroCaso + ": Nina is unreachable";
        }
        return "Case #" + numeroCaso + ": BFS " + bfs.getMoves() + " DFS " + dfs.getMoves();
    }

    public static String formatearMision2(int casoNum, DijkstraResult result) {
        if (!result.isAlcanzable()) {
            return "Case #" + casoNum + ": Nina is very sad";
        }
        return "Case #" + casoNum + ": " + result.getDistancia();
    }

    /*
        MISION 3 (Floyd-Warshall & Bellman-Ford).

        Formato exigido, en este orden EXACTO de precedencia (seccion 5):
            1) "Case #k: Limon blocked the way"   -> D no alcanzable desde S
            2) "Case #k: Infinite churun!"        -> maximo no acotado
            3) "Case #k: <valor>"                 -> el maximo (puede ser negativo)

        El resultado ya trae ese orden resuelto por Mission3Solver (que usa
        Floyd-Warshall como fuente de la respuesta impresa); esta funcion solo
        formatea, igual que las demas de esta clase.
    */
    public static String formatearMision3(int casoNum, Mission3Result resultado) {
        switch (resultado.getEstado()) {
            case BLOQUEADO:
                return "Case #" + casoNum + ": Limon blocked the way";
            case INFINITO:
                return "Case #" + casoNum + ": Infinite churun!";
            default:
                return "Case #" + casoNum + ": " + resultado.getValor();
        }
    }

    /*
        MISION 4 (Kruskal).

        Formato exigido:
            Case #k: <total cost>
        o, si la red no se puede reconectar con los cables disponibles:
            Case #k: Limon cut too many cables

        Kruskal.Resultado.comoTexto() ya resuelve cual de los dos casos
        aplica (usa MENSAJE_SIN_SOLUCION tal cual, caracter por caracter);
        esta funcion solo le agrega el prefijo "Case #k: ", igual que las
        demas misiones.
    */
    public static String formatearMision4(int casoNum, Kruskal.Resultado resultado) {
        return "Case #" + casoNum + ": " + resultado.comoTexto();
    }
}