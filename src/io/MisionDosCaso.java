package io;

import model.Graph;

public final class MisionDosCaso {

    private final Graph grafo;
    private final int origen;
    private final int destino;

    public MisionDosCaso(Graph grafo, int origen, int destino) {
        this.grafo = grafo;
        this.origen = origen;
        this.destino = destino;
    }

    public Graph getGrafo() { return grafo; }
    public int getOrigen() { return origen; }
    public int getDestino() { return destino; }
}