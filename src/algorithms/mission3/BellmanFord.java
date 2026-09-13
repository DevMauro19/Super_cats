package algorithms.mission3;

import Exceptions.EFueraRango;
import model.Edge;
import model.Graph;
import model.WeightedEdge;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

/*
    Mision 3: maximo churun acumulable desde un origen S fijo hacia todos los
    demas nodos, resuelto con Bellman-Ford adaptado a MAXIMIZACION, y usado
    ademas para DETECTAR ciclos de ganancia positiva.

    A diferencia de Floyd-Warshall (que recorre por matriz de pares), este
    algoritmo relaja la lista PLANA de aristas una y otra vez: por eso Graph
    mantiene esa lista por separado (WeightedEdge), en vez de obligar a
    reconstruirla a partir de la lista de adyacencia cada vez.

    Idea de la deteccion de ciclos: si tras relajar todas las aristas N-1
    veces (el maximo numero de aristas que puede tener un camino SIMPLE en un
    grafo de N nodos) una arista TODAVIA permite mejorar una distancia, esa
    mejora solo puede venir de un ciclo que se recorrio una vuelta de mas. Ese
    nodo (y todo lo que sea alcanzable desde el) puede acumular churun sin
    limite con solo dar una vuelta mas cada vez.

    Se comparte el centinela FloydWarshall.NO_ROUTE (en vez de definir uno
    propio) para que Mission3Solver pueda comparar el resultado de este
    algoritmo con el de FloydWarshall sin traducir valores de un lado a otro.

    Complejidad temporal: O(N * M), donde N es el numero de nodos y M el de
    pasajes: N-1 rondas de relajacion (mas una ronda extra para detectar
    ciclos) y cada ronda recorre las M aristas. Con N <= 100 y M <= 5000
    (limites de la seccion 5) son unas 5*10^5 operaciones, muy por debajo del
    limite de tiempo. La propagacion final del contagio "no acotado" agrega
    O(N + M), ya que cada nodo y cada arista se visita a lo sumo una vez.
    Complejidad espacial: O(N + M) -> los arreglos de distancia y marca de no
    acotado, mas la pila de propagacion.

    Esta clase no importa nada de Swing ni de JavaFX: se puede ejecutar
    completa desde un test, como exige la seccion 7.2 del enunciado.
*/
public final class BellmanFord {

    private BellmanFord() {
    }

    public static BellmanFordResult resolver(Graph grafo, int origen) throws EFueraRango {

        grafo.NodoValido(origen);
        int n = grafo.getContadorNodos();

        // distancias[v] = mejor churun conocido desde 'origen' hasta v.
        long[] distancias = new long[n];
        Arrays.fill(distancias, FloydWarshall.NO_ROUTE);
        distancias[origen] = 0; // quedarse en el origen siempre cuesta/rinde 0

        List<WeightedEdge> aristas = grafo.getEdges();

        /*
            N-1 rondas de relajacion, MAXIMIZANDO en vez de minimizando.

            La guarda "distancias[u] == NO_ROUTE -> continue" es la que impide
            hacer aritmetica sobre el centinela (seccion 2.1): mientras u no
            sea alcanzable desde el origen, ninguna arista que salga de u
            puede aportar nada todavia.

            Se corta antes de las N-1 rondas si una vuelta entera no mejoro
            nada: es una optimizacion (menos vueltas sobre grafos chicos o ya
            estabilizados), nunca cambia el resultado final, porque si nada
            mejoro en una ronda completa, tampoco mejorara en las siguientes
            rondas "normales" (sin ciclos de ganancia positiva de por medio).
        */
        for (int ronda = 1; ronda <= n - 1; ronda++) {
            boolean huboMejora = false;
            for (WeightedEdge arista : aristas) {
                int u = arista.getFrom();
                int v = arista.getTo();
                long w = arista.getWeight();

                if (distancias[u] == FloydWarshall.NO_ROUTE) {
                    continue;
                }

                long candidato = distancias[u] + w;
                if (candidato > distancias[v]) {
                    distancias[v] = candidato;
                    huboMejora = true;
                }
            }
            if (!huboMejora) {
                break;
            }
        }

        /*
            RONDA EXTRA (la N-esima): cualquier arista que todavia permita
            mejorar una distancia esta alimentada por un ciclo de ganancia
            positiva. El nodo de destino de esa arista es una "semilla": el,
            y todo lo que sea alcanzable desde el, puede acumular churun sin
            limite.
        */
        boolean[] semilla = new boolean[n];
        for (WeightedEdge arista : aristas) {
            int u = arista.getFrom();
            int v = arista.getTo();
            long w = arista.getWeight();

            if (distancias[u] == FloydWarshall.NO_ROUTE) {
                continue;
            }

            long candidato = distancias[u] + w;
            if (candidato > distancias[v]) {
                semilla[v] = true;
            }
        }

        /*
            Propagacion (BFS/DFS iterativo con pila explicita, sin recursion)
            del contagio "no acotado" a todo lo que sea alcanzable desde una
            semilla, siguiendo las aristas SALIENTES del grafo dirigido.

            Esto es justo el criterio que exige la seccion 5, punto 2: "D es
            no acotado UNICAMENTE si esta entre esos nodos marcados". Un nodo
            alcanzable desde un ciclo positivo pero que ese ciclo no puede
            volver a alcanzar a el mismo (D en particular) NO se marca aqui,
            porque solo se sigue hacia adelante desde las semillas.
        */
        boolean[] noAcotado = new boolean[n];
        Deque<Integer> pendientes = new ArrayDeque<>();

        for (int nodo = 0; nodo < n; nodo++) {
            if (semilla[nodo] && !noAcotado[nodo]) {
                noAcotado[nodo] = true;
                pendientes.push(nodo);
            }
        }

        while (!pendientes.isEmpty()) {
            int u = pendientes.pop();
            for (Edge arista : grafo.getNeightbors(u)) {
                int v = arista.getPara();
                if (!noAcotado[v]) {
                    noAcotado[v] = true;
                    pendientes.push(v);
                }
            }
        }

        return new BellmanFordResult(distancias, noAcotado, origen);
    }
}
