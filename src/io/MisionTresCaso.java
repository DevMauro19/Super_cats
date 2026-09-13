package io;

import model.Graph;

/*
    Contenedor de un caso de prueba de la Mision 3, ya parseado y listo para
    pasarle a Mission3Solver: el grafo DIRIGIDO con los pasajes de churun, mas
    los nodos de origen (S) y destino (D).
*/
public final class MisionTresCaso {

    private final Graph grafo;
    private final int origen;
    private final int destino;

    public MisionTresCaso(Graph grafo, int origen, int destino) {
        this.grafo = grafo;
        this.origen = origen;
        this.destino = destino;
    }

    public Graph getGrafo() {
        return grafo;
    }

    public int getOrigen() {
        return origen;
    }

    public int getDestino() {
        return destino;
    }
}
