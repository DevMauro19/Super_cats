package model;

//Clase encargada de gestionar la coordenada de inicio/fin de la mision 1 (fila,columna)
// Sera inmutable
//Estara implicito dentro de las clases de BFS / DFS


import java.util.Objects;

public final class Punto {

    //Atributos
    private final int fila;
    private final int columna;

    //Constructor
    public Punto(int fila,int columna){
        this.fila=fila;
        this.columna=columna;
    }

    //Getters
    public int getFila(){return fila;}

    public int getColumna(){return columna;}

    // Evitar comparacion por referencia en las clases de BFS y DFS
    // Buscamos evitar cualquier estructura de "visitados"
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o  instanceof Punto)) return false;
        Punto p = (Punto) o;
        return fila == p.fila && columna== p.columna;
    }

    /*  Los objetivos por los que dejamos los metodos de equals() y hashcode()
        - comparar el inicio y el destino dado una entrada
        - Usar la clase punto como valor de retorno del camino
    */

    @Override
    public int hashCode() {
        return Objects.hash(fila,columna);
    }

    @Override
    public String toString() {
        return "(" + fila +", " + columna + ")";
    }

}
