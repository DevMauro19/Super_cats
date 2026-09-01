package model;

/*                              (from,to,weight)
    Arista "plana" representada (de Donde,para,peso) independientemente de la lista de adyacencia.

    Se necesita ademas de Edge porque dos algoritmos recorren el grafo por aristas
    y no por nodo de origen:
        -kruskal (Mission 4) necesita ordenar todas las aristas por peso una sola vez.
        -Bellman-Ford (Mission 3) relaja todas las aristas en cada una de las N-1 rondas.


    Mantener esta lista plana en Graph, en paralelo a la lista de adyacencia.
    evita reconstruir el listado de aristas cada vez que un algoritmo la necesita.

*/

public final class WeightedEdge implements Comparable<WeightedEdge> {

    //Atributos
    private final int from;
    private final int to;
    private final long weight;

    //Constructor
    public WeightedEdge(int from, int to, long weight){

        this.from=from;
        this.to=to;
        this.weight=weight;
    }

    //GETTERS AND SETTERS
    public int getFrom() {return from;}

    public int getTo() {return to;}

    public long getWeight() {return weight;}

    @Override
    public int compareTo(WeightedEdge o) {
        return Long.compare(this.weight,o.weight);
    }

    @Override
    public String toString() {
        return from + "-> "+to + " (w="+weight +")";
    }
}
