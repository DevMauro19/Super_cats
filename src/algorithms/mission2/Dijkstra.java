package algorithms.mission2;

import Exceptions.EFueraRango;
import model.Edge;
import model.Graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public final class Dijkstra {

    private static final class NodoDistancia implements Comparable<NodoDistancia> {
        private final int nodo;
        private final long distancia;

        public NodoDistancia(int nodo, long distancia) {
            this.nodo = nodo;
            this.distancia = distancia;
        }

        @Override
        public int compareTo(NodoDistancia o) {
            return Long.compare(this.distancia, o.distancia);
        }
    }

    private Dijkstra() {}

    public static DijkstraResult resolver(Graph grafo, int origen, int destino) throws EFueraRango {
        grafo.NodoValido(origen);
        grafo.NodoValido(destino);

        int n = grafo.getContadorNodos();
        long[] dist = new long[n];
        int[] previo = new int[n];
        Arrays.fill(dist, Long.MAX_VALUE);
        Arrays.fill(previo, -1);

        PriorityQueue<NodoDistancia> pq = new PriorityQueue<>();

        dist[origen] = 0;
        pq.add(new NodoDistancia(origen, 0));

        while (!pq.isEmpty()) {
            NodoDistancia actual = pq.poll();
            int u = actual.nodo;
            long d = actual.distancia;

            if (d > dist[u]) {
                continue;
            }

            if (u == destino) {
                break;
            }

            for (Edge edge : grafo.getNeightbors(u)) {
                int v = edge.getPara();
                long peso = edge.getWeight();

                if (dist[u] != Long.MAX_VALUE && dist[u] + peso < dist[v]) {
                    dist[v] = dist[u] + peso;
                    previo[v] = u;
                    pq.add(new NodoDistancia(v, dist[v]));
                }
            }
        }

        if (dist[destino] == Long.MAX_VALUE) {
            return new DijkstraResult(-1, Collections.emptyList(), false);
        }

        List<Integer> camino = new ArrayList<>();
        for (int at = destino; at != -1; at = previo[at]) {
            camino.add(at);
        }
        Collections.reverse(camino);

        return new DijkstraResult(dist[destino], camino, true);
    }
}