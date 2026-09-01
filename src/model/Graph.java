package model;

import Exceptions.EFueraRango;
import Exceptions.ENumeroNegativo;

import java.util.ArrayList;
import java.util.List;

/*
    Grafo ponderado generico, reutilizando por Dijkstra (Mission 2), Floyd-Warshall
    y Bellmand-Ford (Mission 3) y Kruskal (Mission 4). Se decide en el constructor
    si es dirigiso o no dirigido:
        -Mission 2 (Dijkstra):      no dirigido -> new Graph (n,false)
        -Mission 3 (Floyd/Bellman):     dirigido -> new Graph (n,true)
        -Mission 4 (Kruskal):       no dirigido -> new Graph (n, false)


    Los nodos se numerna siempre desde 0..N-1 en el modelo. Si el enunciado numra
    intersecciones desde 1 (Mission 4), la conversion (restar 1) se hace con una clase
    de input.

    Internamente se mantiene DOS representaciones sincronizadas:
        1) adjacencia: lista de adjacencia (List<Edge>[]) -> para recorridos dirgidos por nodo (Dijkstra, Floyd-Warshall).
        2) edges: lista planade weightedEdge -> para algoritmos que iteran sobre todas las  aristas (Kruskal, Bellman-Ford).

    Se agregan juntas en addEdge() para que desincronizadas.

*/

public final class Graph {

    //Atributos
    private final int contadorNodos;
    private final boolean dirigido;
    private final List<Edge>[]adjacencia;
    private final List <WeightedEdge> edges;

    @SuppressWarnings("unchecked") //ES comun usarlo cuando trabajamos con estructuras como nodos
    public Graph(int contadorNodos,boolean dirigido)throws ENumeroNegativo{
        if(contadorNodos<0){throw new ENumeroNegativo("El contador de nodos no puede ser negativo");}

        this.contadorNodos=contadorNodos;
        this.dirigido=dirigido;
        this.adjacencia=new List[contadorNodos];
        for(int i=0; i<contadorNodos;i++){
            adjacencia[i]=new ArrayList<>();
        }
        this.edges=new ArrayList<>();
    }

    /*                      (de donde,para)
        Agrega una conexion from-to con el peso dado.
        Si el grafo es dirigido: solo usa from-to.
        Si no dirigifo: from -> to y to ->from (dos entradas en la lista de adyacencia,
        pero Una sola WeightedEdge en la lista plana, ya que Kruskal solo necesita considerar cada cable una vez).

        Aristtas repetidas y auto-ciclos (from==to) estan permitidos, como se indican en las
        misiones 2,3,4.
    */

    public void addEdge(int from,int to,long weight) throws ENumeroNegativo {
        NodoValido(from);
        NodoValido(to);
        adjacencia[from].add(new Edge(to,weight));
        if(!dirigido){
            adjacencia[to].add(new Edge(from,weight));
        }
        edges.add(new WeightedEdge(from, to, weight));
    }

    public int getContadorNodos() {return contadorNodos;}

    public boolean isDirigido() {return dirigido;}

    /* Vecinos salientes de 'nodo' (para Dijkstra, Floyd-Warshall, BFS/DFS si se usara sobre grasfos)*/

    public List<Edge> getNeightbors(int nodo){
        NodoValido(nodo);
        return adjacencia[nodo];
    }

    /* Lista plana de todas las aristas, sin duplicar en no dirgido (para Kruskal y Bellman-Ford)*/

    public List<WeightedEdge> getEdges(){
        return edges;
    }

    public void NodoValido(int nodo)throws EFueraRango{
        if(nodo<0 || nodo>=contadorNodos){
            throw new EFueraRango("Nodo fuera de rango: "+nodo+" (contadorNodos="+contadorNodos+")");
        }
    }
}
