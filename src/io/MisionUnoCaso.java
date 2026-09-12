package io;

import model.Grid;
import model.Punto;

/*
    Representa un caso de prueba ya parseado de la Mision 1: la grilla con
    sus bombas, el punto de inicio y el punto de destino. Es solo un
    contenedor de datos (sin logica) para que Input.leerMision1 pueda
    devolver los tres juntos por cada caso, en vez de tres listas separadas.
*/
public final class MisionUnoCaso {

    private final Grid grid;
    private final Punto inicio;
    private final Punto destino;

    public MisionUnoCaso(Grid grid, Punto inicio, Punto destino) {
        this.grid = grid;
        this.inicio = inicio;
        this.destino = destino;
    }

    public Grid getGrid() {
        return grid;
    }

    public Punto getInicio() {
        return inicio;
    }

    public Punto getDestino() {
        return destino;
    }
}