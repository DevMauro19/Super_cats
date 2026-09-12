package algorithms.mission2;

import java.util.List;

public final class DijkstraResult {

    private final long distancia;
    private final List<Integer> camino;
    private final boolean alcanzable;

    public DijkstraResult(long distancia, List<Integer> camino, boolean alcanzable) {
        this.distancia = distancia;
        this.camino = camino;
        this.alcanzable = alcanzable;
    }

    public long getDistancia() { return distancia; }
    public List<Integer> getCamino() { return camino; }
    public boolean isAlcanzable() { return alcanzable; }
}