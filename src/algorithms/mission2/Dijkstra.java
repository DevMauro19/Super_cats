package algorithms.mission2;

import Exceptions.EFueraRango;
import model.Edge;
import model.Graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

/*
    Mision 2: ruta de menor costo entre dos nodos, resuelta con el algoritmo de Dijkstra.

    Estrategia voraz: se procesa siempre el nodo pendiente MAS CERCANO al origen.
    Cuando un nodo sale de la cola de prioridad su distancia ya es definitiva, y eso
    es cierto UNICAMENTE porque ningun peso es negativo: cualquier otra ruta hacia el
    tendria que pasar por nodos que estan mas lejos, y sumarles un peso no negativo
    solo puede encarecerla. Con una arista negativa ese razonamiento se cae, y por eso
    la Mision 3 (que si admite negativos) usa Bellman-Ford en lugar de este algoritmo.

    Complejidad temporal: O(C log N), donde N es el numero de nodos y C el de conexiones.
    Cada arista puede insertar una entrada en la cola y cada operacion del heap cuesta
    O(log N). Un barrido lineal buscando el minimo daria O(N^2), que con N = 10.000 son
    10^8 operaciones: el enunciado lo rechaza explicitamente.
    Complejidad espacial: O(N + C) -> los arreglos dist y previo, mas la cola.

    Esta clase no importa nada de Swing ni de JavaFX: se puede ejecutar completa desde
    un test, como exige la seccion 7.2 del enunciado.
*/
public final class Dijkstra {

    /*
        Entrada de la cola de prioridad: un nodo junto con la distancia con la que
        se lo encolo. Se necesita una clase propia (y no solo el numero de nodo)
        porque el heap tiene que poder comparar por distancia.
    */
    private static final class NodoDistancia implements Comparable<NodoDistancia> {

        private final int nodo;
        private final long distancia;

        public NodoDistancia(int nodo, long distancia) {
            this.nodo = nodo;
            this.distancia = distancia;
        }

        /*
            Ordena por DISTANCIA, nunca por numero de nodo: asi el "menor" que
            entrega la PriorityQueue es el nodo pendiente mas cercano al origen,
            que es justo el que Dijkstra debe procesar a continuacion.

            Se usa Long.compare y no una resta del tipo (int)(a - b): con valores
            grandes esa resta se desborda y devuelve el signo equivocado, lo que
            romperia el orden del heap en silencio.
        */
        @Override
        public int compareTo(NodoDistancia o) {
            return Long.compare(this.distancia, o.distancia);
        }
    }

    // Clase de utilidades: no tiene sentido instanciarla.
    private Dijkstra() {}

    /*
        Calcula la ruta mas barata de 'origen' a 'destino' sobre el grafo dado.
        Devuelve el costo total, el camino completo (para que la GUI lo pueda
        resaltar, seccion 7.3) y si el destino es alcanzable.
    */
    public static DijkstraResult resolver(Graph grafo, int origen, int destino) throws EFueraRango {

        // Los dos extremos tienen que existir dentro del grafo.
        grafo.NodoValido(origen);
        grafo.NodoValido(destino);

        int n = grafo.getContadorNodos();

        // dist[v] = mejor distancia conocida desde el origen hasta v.
        // Es long y no int porque el acumulado puede llegar a ~10^11
        // (100.000 conexiones por 1.000.000 de costo), muy por encima del rango de int.
        long[] dist = new long[n];

        // previo[v] = desde que nodo se llego a v. Sirve para reconstruir el camino.
        int[] previo = new int[n];

        // Long.MAX_VALUE hace de "infinito": todavia no se como llegar a ese nodo.
        Arrays.fill(dist, Long.MAX_VALUE);

        // -1 significa "sin predecesor". Marca el origen y corta la reconstruccion.
        Arrays.fill(previo, -1);

        PriorityQueue<NodoDistancia> pq = new PriorityQueue<>();

        // Llegar al origen desde el origen cuesta 0. Es el unico punto de partida.
        dist[origen] = 0;
        pq.add(new NodoDistancia(origen, 0));

        while (!pq.isEmpty()) {

            // El heap entrega siempre el nodo pendiente mas cercano al origen.
            NodoDistancia actual = pq.poll();
            int u = actual.nodo;
            long d = actual.distancia;

            /*
                DESCARTE PEREZOSO (lazy deletion).

                java.util.PriorityQueue no permite bajar la prioridad de un elemento
                que ya esta adentro, asi que cuando encontramos una ruta mas barata a
                un nodo no lo "actualizamos": insertamos una copia nueva y dejamos la
                vieja. Cuando la vieja sale, su distancia es mayor que la que ya
                conocemos y se descarta aqui sin procesarla.

                Sin este 'continue' el algoritmo daria el MISMO resultado correcto,
                solo que reprocesando nodos de mas. Es una optimizacion, no una
                condicion de correccion.
            */
            if (d > dist[u]) {
                continue;
            }

            /*
                CORTE TEMPRANO.

                Si el nodo que acaba de salir es el destino, su distancia ya es
                definitiva y no hace falta seguir explorando el resto del grafo.
                Es valido por la misma razon que el algoritmo entero: ninguna ruta
                que pase por nodos mas lejanos puede resultar mas barata con pesos
                no negativos.
            */
            if (u == destino) {
                break;
            }

            for (Edge edge : grafo.getNeightbors(u)) {

                int v = edge.getPara();
                long peso = edge.getWeight();

                /*
                    RELAJACION: llegar a v pasando por u sale mas barato que la
                    mejor ruta que conociamos hasta v?

                    La guarda dist[u] != Long.MAX_VALUE cumple la seccion 2.1 del
                    enunciado: nunca hacer aritmetica sobre el centinela de "sin
                    ruta". Long.MAX_VALUE + peso se desborda y queda negativo, con
                    lo que un nodo inalcanzable pasaria a verse como el mas barato
                    de todos. En este bucle u siempre sale de la cola con distancia
                    finita, asi que la guarda esta como defensa y no porque el flujo
                    actual la necesite.
                */
                if (dist[u] != Long.MAX_VALUE && dist[u] + peso < dist[v]) {

                    dist[v] = dist[u] + peso;   // nueva mejor distancia conocida
                    previo[v] = u;              // y por donde se llego

                    // Se encola la version mejorada; la entrada anterior de v, si
                    // existia, quedara descartada por el 'continue' de arriba.
                    pq.add(new NodoDistancia(v, dist[v]));
                }
            }
        }

        /*
            Si dist[destino] quedo en el centinela es que nunca se relajo: no existe
            ninguna ruta. El mismo valor sirve de "infinito" durante el calculo y de
            senal de inalcanzable al final, sin necesidad de una bandera aparte.
            La capa de salida traduce esto a "Nina is very sad".
        */
        if (dist[destino] == Long.MAX_VALUE) {
            return new DijkstraResult(-1, Collections.emptyList(), false);
        }

        // Se reconstruye el camino hacia atras, saltando de cada nodo a su previo
        // hasta topar con el -1 que marca el origen.
        List<Integer> camino = new ArrayList<>();
        for (int at = destino; at != -1; at = previo[at]) {
            camino.add(at);
        }

        // Venia del destino al origen: se invierte para entregarlo en orden de lectura.
        Collections.reverse(camino);

        return new DijkstraResult(dist[destino], camino, true);
    }
}
