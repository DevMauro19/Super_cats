package algorithms.mission4;

import Exceptions.ENumeroNegativo;
import model.Graph;
import model.WeightedEdge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
    Mision 4: reconectar la red con la menor cantidad de cable posible.
    Es el problema del Arbol de Recubrimiento Minimo (MST) resuelto con Kruskal.

    Estrategia avara: se revisan los cables del mas barato al mas caro y se
    acepta cada uno solo si une dos pedazos de red que todavia estaban separados.
    Si los dos extremos ya estaban conectados, el cable formaria un ciclo y sobra.

    Complejidad temporal: O(C log C), dominada por el ordenamiento de los C cables.
    Las C operaciones de union-find aportan solo O(C * alfa(N)), practicamente lineal.
    Complejidad espacial: O(N + C) -> la copia de cables mas los arreglos del union-find.

    Por que Kruskal y no Prim: la entrada llega como una lista plana de cables,
    que es exactamente lo que Kruskal consume. Prim necesitaria recorrer por
    vecinos de cada nodo y una cola de prioridad, sin ganar nada en estos limites.

    Esta clase no importa nada de Swing ni de JavaFX: se puede ejecutar
    completa desde un test, como exige la seccion 7.2 del enunciado.
*/
public final class Kruskal {

    // Mensaje exacto que pide el enunciado cuando la red no se puede reconstruir.
    // ASCII plano, sin tildes y sin punto final: se compara caracter por caracter.
    public static final String MENSAJE_SIN_SOLUCION = "Limon cut too many cables";

    // Clase de utilidades: no tiene sentido instanciarla.
    private Kruskal() {
    }

    /*
        Ejecuta Kruskal sobre el grafo recibido y devuelve el resultado completo:
        el costo total, si se logro conectar todo, y cuales cables se eligieron
        (esa lista es la que la GUI necesita para resaltar el dibujo).
    */
    public static Resultado ejecutar(Graph grafo) throws ENumeroNegativo {

        int cantidadNodos = grafo.getContadorNodos();

        // Copia defensiva: vamos a ordenar la lista y no queremos alterar
        // el orden interno del Graph, que otras partes del proyecto tambien usan.
        List<WeightedEdge> cables = new ArrayList<>(grafo.getEdges());

        // Corazon del algoritmo: del mas barato al mas caro.
        // Usa el compareTo de WeightedEdge, que ya compara por peso con Long.compare.
        Collections.sort(cables);

        // Un conjunto por nodo. A medida que aceptemos cables se iran fusionando.
        Union conjuntos = new Union(cantidadNodos);

        // Cables que terminan formando el arbol. Sirven para el costo y para el dibujo.
        List<WeightedEdge> seleccionados = new ArrayList<>();

        // long y no int: 100.000 cables por 1.000.000 de costo se pasa del rango de int.
        long costoTotal = 0L;

        for (WeightedEdge cable : cables) {

            // Un arbol que conecta N nodos usa exactamente N-1 aristas.
            // Al llegar a esa cantidad ya esta todo conectado y los cables
            // que faltan solo serian mas caros: cortamos.
            if (seleccionados.size() == cantidadNodos - 1) {
                break;
            }

            // unir() devuelve true solo si los extremos estaban en clanes distintos.
            // Si devuelve false, este cable cerraria un ciclo y se descarta solo.
            if (conjuntos.unir(cable.getFrom(), cable.getTo())) {

                // Cable aceptado: entra al arbol.
                seleccionados.add(cable);

                // Y su costo suma al total.
                costoTotal += cable.getWeight();
            }
        }

        // Criterio de exito: conseguimos las N-1 aristas.
        // El caso N <= 1 se trata aparte porque con un solo nodo (o ninguno)
        // no hace falta ningun cable y la respuesta correcta es 0.
        boolean conectado = (cantidadNodos <= 1) || (seleccionados.size() == cantidadNodos - 1);

        return new Resultado(conectado, costoTotal, seleccionados);
    }

    /*
        Objeto de salida del algoritmo.
        Se devuelve todo junto para que la GUI pueda mostrar el numero
        y ademas resaltar los cables usados, sin tener que recalcular nada.
    */
    public static final class Resultado {

        // false cuando quedaron intersecciones aisladas.
        private final boolean conectado;

        // Suma de los pesos de los cables elegidos. Solo es valida si conectado es true.
        private final long costoTotal;

        // Los cables que forman el MST, en el orden en que fueron aceptados.
        private final List<WeightedEdge> cablesSeleccionados;

        public Resultado(boolean conectado, long costoTotal, List<WeightedEdge> cablesSeleccionados) {
            this.conectado = conectado;
            this.costoTotal = costoTotal;

            // Lista de solo lectura: nadie de afuera puede modificar el resultado despues.
            this.cablesSeleccionados = Collections.unmodifiableList(new ArrayList<>(cablesSeleccionados));
        }

        public boolean isConectado() {
            return conectado;
        }

        public long getCostoTotal() {
            return costoTotal;
        }

        public List<WeightedEdge> getCablesSeleccionados() {
            return cablesSeleccionados;
        }

        /*
            Devuelve la parte del texto que va despues de "Case #k: ".
            El prefijo "Case #k: " lo arma io/Output, que es quien lleva
            la cuenta del numero de caso. Asi el algoritmo no sabe nada de formato de salida.
        */
        public String comoTexto() {
            return conectado ? String.valueOf(costoTotal) : MENSAJE_SIN_SOLUCION;
        }
    }
}