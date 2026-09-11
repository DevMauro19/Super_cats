package algorithms.mission4;

import Exceptions.EFueraRango;
import Exceptions.ENumeroNegativo;

/*
    Estructura Union-Find (conjuntos disjuntos) con las dos optimizaciones
    que exige el enunciado: compresion de caminos y union por tamano.

    Responde una sola pregunta, pero la responde muy rapido:
    "estos dos elementos ya pertenecen al mismo grupo?"

    Analogia: cada elemento lleva una manilla con el nombre de su jefe de clan.
    Para saber si dos elementos estan conectados basta comparar sus jefes.

    Complejidad: cada operacion es O(alfa(n)) amortizado, donde alfa es la
    inversa de Ackermann; para cualquier n real alfa(n) < 5, o sea O(1) practico.
    Espacio: O(n) -> dos arreglos de enteros de tamano n.
*/
public final class Union {

    // padre[i] guarda quien es el "jefe inmediato" de i.
    // Si padre[i] == i, entonces i es la raiz (el jefe del clan).
    private final int[] padre;

    // tamano[i] solo tiene sentido cuando i es raiz: cuantos elementos tiene su clan.
    // Se usa para decidir cual clan se cuelga de cual al fusionar.
    private final int[] tamano;

    // Cuantos grupos separados existen en este momento.
    // Arranca en n (cada elemento solo) y baja en 1 con cada union efectiva.
    private int componentes;

    /*
        Crea la estructura con 'cantidadElementos' elementos, cada uno
        en su propio grupo individual.
    */
    public Union(int cantidadElementos) throws ENumeroNegativo {

        // Un numero negativo de elementos no tiene sentido: se corta de una.
        if (cantidadElementos < 0) {
            throw new ENumeroNegativo("La cantidad de elementos no puede ser negativa: " + cantidadElementos);
        }

        // Reservamos los dos arreglos de una vez, ya sabemos el tamano final.
        this.padre = new int[cantidadElementos];
        this.tamano = new int[cantidadElementos];

        // Estado inicial: cada elemento es su propio jefe y su clan tiene un solo miembro.
        for (int i = 0; i < cantidadElementos; i++) {
            padre[i] = i;
            tamano[i] = 1;
        }

        // Al principio hay tantos grupos como elementos.
        this.componentes = cantidadElementos;
    }

    /*
        FIND: devuelve la raiz (el jefe de clan) del elemento dado.
        Va iterativo a proposito: con 10.000 nodos una version recursiva
        podria crecer mucho en pila, y el enunciado ya castiga la recursion en la mision 1.
    */
    public int buscar(int elemento) {

        // Si el elemento no existe, es un error de entrada y no una condicion normal.
        validar(elemento);

        // PRIMERA PASADA: subir por la cadena de padres hasta llegar a la raiz.
        int raiz = elemento;
        while (padre[raiz] != raiz) {
            raiz = padre[raiz];
        }

        // SEGUNDA PASADA (compresion de caminos): volvemos a recorrer el mismo
        // camino, pero ahora reapuntando cada nodo directamente a la raiz.
        // Asi la proxima consulta sobre cualquiera de ellos es inmediata.
        int actual = elemento;
        while (padre[actual] != raiz) {

            // Guardamos a donde ibamos antes de sobrescribir el puntero.
            int siguiente = padre[actual];

            // Atajo: este nodo ahora apunta directo al jefe.
            padre[actual] = raiz;

            // Seguimos subiendo con el puntero viejo.
            actual = siguiente;
        }

        return raiz;
    }

    /*
        UNION: fusiona los grupos de 'a' y 'b'.
        Devuelve true si realmente se unieron dos grupos distintos,
        y false si ya estaban juntos (ese false es justo lo que Kruskal
        usa para descartar un cable que formaria ciclo).
    */
    public boolean unir(int a, int b) {

        // Buscamos el jefe de cada uno. Aqui ya se valida el rango.
        int raizA = buscar(a);
        int raizB = buscar(b);

        // Mismo jefe = mismo clan: no hay nada que unir.
        // Esto tambien cubre gratis los auto-ciclos del tipo "1 1 5".
        if (raizA == raizB) {
            return false;
        }

        // UNION POR TAMANO: el clan pequeno se cuelga del grande, nunca al reves.
        // Si estan al reves los intercambiamos para que raizA sea siempre el grande.
        if (tamano[raizA] < tamano[raizB]) {
            int intercambio = raizA;
            raizA = raizB;
            raizB = intercambio;
        }

        // El jefe chico pasa a depender del jefe grande.
        padre[raizB] = raizA;

        // El clan grande absorbe el peso del chico.
        tamano[raizA] += tamano[raizB];

        // Dos grupos se volvieron uno.
        componentes--;

        return true;
    }

    /*
        Consulta pura: estan 'a' y 'b' en el mismo grupo?
        No modifica la estructura (mas alla de la compresion, que es transparente).
    */
    public boolean estanConectados(int a, int b) {
        return buscar(a) == buscar(b);
    }

    // Cuantos grupos separados quedan. Si vale 1, todo esta conectado.
    public int getComponentes() {
        return componentes;
    }

    // Cuantos elementos tiene el grupo al que pertenece 'elemento'.
    public int getTamanoComponente(int elemento) {
        return tamano[buscar(elemento)];
    }

    // Validacion de rango centralizada, para no repetir el if en cada metodo.
    private void validar(int elemento) {
        if (elemento < 0 || elemento >= padre.length) {
            throw new EFueraRango("Elemento fuera de rango: " + elemento + " (total=" + padre.length + ")");
        }
    }
}