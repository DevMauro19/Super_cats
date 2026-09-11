package algorithms.mission1;

import model.Grid;
import model.Punto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * BFS y DFS sobre la grilla de la Mision 1 (rescate de Nina).
 *
 * Complejidad de ambos: O(R*C) en tiempo y espacio, ya que cada celda se
 * visita a lo sumo una vez y las estructuras auxiliares son del tamano de
 * la grilla. Es la eleccion correcta porque el grafo es implicito (sin
 * pesos, 4 vecinos por celda) y BFS/DFS son exactamente los algoritmos
 * pensados para explorar ese tipo de grafo sin construir aristas explicitas.
 *
 * Los vecinos NUNCA se materializan como lista de adyacencia: se calculan al
 * vuelo a partir de (row, col), porque con grillas de hasta 10^6 celdas
 * almacenar hasta 4*10^6 aristas desperdiciaria memoria sin necesidad.
 *
 * Las celdas se identifican con un entero "id = row*cols + col" en vez de
 * usar objetos Point en las estructuras internas (visited, parent, pilas):
 * evita boxing y creacion de millones de objetos en el peor caso.
 */
public final class BFSDFSSolver {

    /** Orden fijo exigido por el enunciado: arriba, abajo, izquierda, derecha. */
    private static final int[] DR = {-1, 1, 0, 0};
    private static final int[] DC = {0, 0, -1, 1};

    private BFSDFSSolver() {
    }

    /**
     * BFS: siempre encuentra el camino MAS CORTO en un grafo no ponderado,
     * porque explora la grilla nivel por nivel (todas las celdas a distancia
     * d antes que cualquiera a distancia d+1). Usa una cola FIFO.
     */
    public static PathResult bfs(Grid grid, Punto start, Punto dest) {
        if (grid.hayBomba(start) || grid.hayBomba(dest)) {
            return PathResult.unreachable();
        }

        int cols = grid.getColumnas();
        int total = grid.getFilas() * cols;
        int startId = encode(start, cols);
        int destId = encode(dest, cols);

        if (startId == destId) {
            return PathResult.of(0, List.of(start));
        }

        boolean[] visited = new boolean[total];
        int[] parent = new int[total];
        Arrays.fill(parent, -1);

        // Cola FIFO implementada como arreglo circular de ids (evita el overhead
        // de boxing de Queue<Integer> para grillas de hasta un millon de celdas).
        int[] queue = new int[total];
        int head = 0;
        int tail = 0;

        visited[startId] = true;
        queue[tail++] = startId;

        while (head < tail) {
            int currentId = queue[head++];
            int row = currentId / cols;
            int col = currentId % cols;

            for (int dir = 0; dir < 4; dir++) {
                int nr = row + DR[dir];
                int nc = col + DC[dir];
                if (!grid.esCaminable(nr, nc)) {
                    continue;
                }
                int neighborId = encode(nr, nc, cols);
                if (visited[neighborId]) {
                    continue;
                }
                visited[neighborId] = true;
                parent[neighborId] = currentId;

                if (neighborId == destId) {
                    List<Punto> path = buildPathFromParents(parent, destId, cols);
                    return PathResult.of(path.size() - 1, path);
                }
                queue[tail++] = neighborId;
            }
        }
        return PathResult.unreachable();
    }

    /**
     * DFS NO recursivo: simula el call stack manualmente para reproducir
     * EXACTAMENTE el mismo orden de visita que una version recursiva con el
     * orden fijo arriba-abajo-izquierda-derecha. Esto es necesario porque el
     * enunciado exige un resultado determinista y comparable, y porque una
     * version recursiva reventaria el stack con grillas de hasta 10^6 celdas.
     *
     * Cada "frame" de la pila guarda (celda, proxima direccion a intentar).
     * Al empujar un vecino se avanza en profundidad (como una llamada
     * recursiva); cuando las 4 direcciones de un frame se agotan, se hace
     * pop (equivalente al "return" de esa llamada). Esto es distinto -y
     * correcto- frente al error comun de empujar los 4 vecinos de una vez:
     * eso invertiria el orden (una pila es LIFO) y no coincidiria con la
     * recursion.
     */
    public static PathResult dfs(Grid grid, Punto start, Punto dest) {
        if (grid.hayBomba(start) || grid.hayBomba(dest)) {
            return PathResult.unreachable();
        }

        int cols = grid.getColumnas();
        int total = grid.getFilas() * cols;
        int startId = encode(start, cols);
        int destId = encode(dest, cols);

        if (startId == destId) {
            return PathResult.of(0, List.of(start));
        }

        boolean[] visited = new boolean[total];
        int[] stackCell = new int[total];
        int[] stackNextDir = new int[total]; // proxima direccion (0..3) a probar en cada frame
        int sp = 0; // stack pointer: numero de frames activos

        visited[startId] = true;
        stackCell[sp] = startId;
        stackNextDir[sp] = 0;
        sp++;

        while (sp > 0) {
            int top = sp - 1;

            if (stackNextDir[top] == 4) {
                // Las 4 direcciones de este frame ya se probaron: backtrack.
                sp--;
                continue;
            }

            int dir = stackNextDir[top]++;
            int currentId = stackCell[top];
            int row = currentId / cols;
            int col = currentId % cols;
            int nr = row + DR[dir];
            int nc = col + DC[dir];

            if (!grid.esCaminable(nr, nc)) {
                continue;
            }
            int neighborId = encode(nr, nc, cols);
            if (visited[neighborId]) {
                continue;
            }

            visited[neighborId] = true;
            stackCell[sp] = neighborId;
            stackNextDir[sp] = 0;
            sp++;

            if (neighborId == destId) {
                // La pila actual, de abajo (start) a arriba (dest), ES el camino
                // que tomo el DFS: no hay que reconstruirlo con punteros a padres.
                List<Punto> path = buildPathFromStack(stackCell, sp, cols);
                return PathResult.of(path.size() - 1, path);
            }
        }
        return PathResult.unreachable();
    }

    private static int encode(Punto p, int cols) {
        return encode(p.getFila(), p.getColumna(), cols);
    }

    private static int encode(int row, int col, int cols) {
        return row * cols + col;
    }

    private static Punto decode(int id, int cols) {
        return new Punto(id / cols, id % cols);
    }

    private static List<Punto> buildPathFromParents(int[] parent, int destId, int cols) {
        List<Punto> reversed = new ArrayList<>();
        int currentId = destId;
        while (currentId != -1) {
            reversed.add(decode(currentId, cols));
            currentId = parent[currentId];
        }
        Collections.reverse(reversed);
        return reversed;
    }

    private static List<Punto> buildPathFromStack(int[] stackCell, int size, int cols) {
        List<Punto> path = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            path.add(decode(stackCell[i], cols));
        }
        return path;
    }
}