package algorithms.mission1;

import model.Punto;

import java.util.Collections;
import java.util.List;

/**
 * Resultado de resolver la Mision 1 con BFS o DFS: si Nina es alcanzable,
 * cuantos movimientos toma el camino encontrado, y cual es ese camino
 * completo (para que la GUI lo pueda dibujar, seccion 7.3).
 *
 * Es inmutable y NO imprime nada: formatear "Case #k: ..." o "Nina is
 * unreachable" es responsabilidad de la capa de salida (OutputFormatter),
 * no del algoritmo. Esto es lo que permite probar BFSDFSSolver con un test
 * normal que compara valores, sin abrir ninguna ventana (seccion 7.2).
 */
public final class PathResult {

    private final boolean reachable;
    private final int moves;
    private final List<Punto> path;

    private PathResult(boolean reachable, int moves, List<Punto> path) {
        this.reachable = reachable;
        this.moves = moves;
        this.path = path;
    }

    public static PathResult unreachable() {
        return new PathResult(false, -1, Collections.emptyList());
    }

    public static PathResult of(int moves, List<Punto> path) {
        return new PathResult(true, moves, path);
    }

    public boolean isReachable() {
        return reachable;
    }

    /** Numero de movimientos del camino encontrado. -1 si Nina no es alcanzable. */
    public int getMoves() {
        return moves;
    }

    /** Camino completo desde el inicio hasta el destino, ambos incluidos. Vacio si no es alcanzable. */
    public List<Punto> getPath() {
        return path;
    }
}