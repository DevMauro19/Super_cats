package test;

import Exceptions.ENumeroNegativo;
import algorithms.mission1.BFSDFSSolver;
import algorithms.mission1.PathResult;
import model.Grid;
import model.Punto;

/**
 * Prueba manual (sin JUnit, para correr con "java BFS_DFS_TEST") que reproduce
 * EXACTAMENTE el ejemplo de la seccion 3 del enunciado: grilla 10x10, las
 * mismas bombas, inicio (0,0), destino (9,9). El resultado esperado es
 * "Case #1: BFS 18 DFS 32". Cuando tengan JUnit configurado, conviertan esto
 * en un @Test real; mientras tanto sirve para validar el solver localmente.
 */
public final class BFS_DFS_TEST {

    public static void main(String[] args) {
        try {
            Grid grid = new Grid(10, 10);

            // Bombas exactamente como en el input de muestra del PDF
            setBombs(grid, 0, 2);
            setBombs(grid, 1, 2);
            setBombs(grid, 2, 9);
            setBombs(grid, 3, 1, 7);
            setBombs(grid, 5, 3, 6, 9);
            setBombs(grid, 6, 0, 1, 2, 7);
            setBombs(grid, 7, 0, 3, 8);

            Punto start = new Punto(0, 0);
            Punto dest = new Punto(9, 9);

            PathResult bfsResult = BFSDFSSolver.bfs(grid, start, dest);
            PathResult dfsResult = BFSDFSSolver.dfs(grid, start, dest);

            System.out.println("BFS moves = " + bfsResult.getMoves() + " (esperado 18)");
            System.out.println("DFS moves = " + dfsResult.getMoves() + " (esperado 32)");

            boolean pass = bfsResult.isReachable()
                    && bfsResult.getMoves() == 18
                    && dfsResult.isReachable()
                    && dfsResult.getMoves() == 32;

            System.out.println(pass ? "PASS" : "FAIL");
            if (!pass) {
                System.exit(1);
            }
        } catch (ENumeroNegativo e) {
            throw new AssertionError("La prueba falló por parámetros inválidos en Grid", e);
        }
    }

    private static void setBombs(Grid grid, int row, int... cols) {
        for (int col : cols) {
            grid.setBombas(row, col);
        }
    }
}