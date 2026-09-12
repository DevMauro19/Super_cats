package io;

import algorithms.mission1.PathResult;

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
}