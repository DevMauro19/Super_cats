package io;

import Exceptions.EEntradaInvalida;
import Exceptions.ENumeroNegativo;
import model.Graph;
import model.Punto;
import model.Grid;

import java.util.ArrayList;
import java.util.List;

/*
    Lectura de las entradas que el usuario pega como texto plano en la GUI.

    Regla general del enunciado (seccion 2.2): la entrada se procesa como un
    FLUJO DE TOKENS separados por espacios en blanco. No se asume un numero
    fijo de tokens por linea, y se toleran lineas en blanco y espacios sobrantes.
    Por eso aqui nunca se usa readLine(): se parte todo el texto de una vez y
    se va consumiendo token por token, sin importar donde caigan los saltos de linea.

    Cada mision agrega su propio metodo estatico a esta clase. Son independientes
    entre si, asi que varios integrantes pueden trabajar en paralelo sin pisarse.
*/
public final class Input {

    // Entrada de ejemplo de la mision 1, tal cual aparece en el enunciado (seccion 3).
    public static final String EJEMPLO_MISSION1 =
            "10 10\n" +
                    "9\n" +
                    "0 1 2\n" +
                    "1 1 2\n" +
                    "2 2 2 9\n" +
                    "3 2 1 7\n" +
                    "5 3 3 6 9\n" +
                    "6 4 0 1 2 7\n" +
                    "7 3 0 3 8\n" +
                    "8 2 7 9\n" +
                    "9 3 2 3 4\n" +
                    "0 0\n" +
                    "9 9\n" +
                    "0 0\n";

    // Entrada de ejemplo de la mision 4, tal cual aparece en el enunciado.
    // La usa el boton "load sample" que exige la seccion 7.1.
    public static final String EJEMPLO_MISSION4 =
            "1\n" +
                    "4\n" +
                    "5\n" +
                    "1 2 10\n" +
                    "2 3 20\n" +
                    "3 4 30\n" +
                    "4 1 40\n" +
                    "1 3 15\n";

    // Clase de utilidades: no se instancia.
    private Input() {
    }

    /*
       MISION 1 (BFS/DFS - Rescate de Nina).

       Formato esperado por cada caso, repetido hasta encontrar R=0 y C=0:
           R C
           filasConBombas
           (por cada fila con bombas: fila cantidadBombas col1 col2 ...)
           filaInicio columnaInicio
           filaDestino columnaDestino

       El caso con R=0 y C=0 marca el fin de la entrada y NO se procesa
       (seccion 3: "you must not process that test case").

       Devuelve una lista de MisionUnoCaso, cada uno con su Grid ya armado
       y los puntos de inicio/destino, listos para pasarle a BFS/DFS.

       ENumeroNegativo puede propagarse desde el constructor de Grid si en
       algun momento llegara un valor negativo hasta ahi; en la practica no
       deberia pasar porque aqui mismo se valida el rango 1..1000 antes de
       construir el Grid, pero se deja declarada por consistencia con
       leerMission4 y como defensa adicional.
   */
    public static List<MisionUnoCaso> leerMision1(String texto) throws EEntradaInvalida, ENumeroNegativo {

        LectorTokens lector = new LectorTokens(texto);
        List<MisionUnoCaso> casos = new ArrayList<>();

        while (true) {

            int filas = lector.siguienteEntero("R (numero de filas) de un caso de prueba");
            int columnas = lector.siguienteEntero("C (numero de columnas) de un caso de prueba");

            // Sentinela de fin de entrada: no se procesa como caso real.
            if (filas == 0 && columnas == 0) {
                break;
            }

            // El enunciado exige 1 <= R,C <= 1000. Grid solo protege contra
            // valores negativos (ENumeroNegativo); el limite superior y el
            // cero son una regla del FORMATO de esta mision especifica, no
            // un invariante del modelo Grid, asi que se valida aqui.
            if (filas < 1 || filas > 1000 || columnas < 1 || columnas > 1000) {
                throw new EEntradaInvalida("R y C deben estar entre 1 y 1000 (se leyo R="
                        + filas + ", C=" + columnas + ")");
            }

            Grid grid = new Grid(filas, columnas);

            int filasConBombas = lector.siguienteEntero("la cantidad de filas con bombas");
            if (filasConBombas < 0 || filasConBombas > filas) {
                throw new EEntradaInvalida("La cantidad de filas con bombas debe estar entre 0 y R="
                        + filas + " (se leyo " + filasConBombas + ")");
            }

            for (int i = 0; i < filasConBombas; i++) {

                int fila = lector.siguienteEntero("el numero de una fila con bombas");
                validarCoordenada(fila, filas, "fila con bombas");

                int cantidadBombas = lector.siguienteEntero("la cantidad de bombas en la fila " + fila);
                if (cantidadBombas < 0 || cantidadBombas > columnas) {
                    throw new EEntradaInvalida("La fila " + fila + " indica " + cantidadBombas
                            + " bombas, pero la grilla solo tiene " + columnas + " columnas");
                }

                for (int b = 0; b < cantidadBombas; b++) {
                    int col = lector.siguienteEntero("la columna de una bomba en la fila " + fila);
                    validarCoordenada(col, columnas, "columna de una bomba");
                    grid.setBombas(fila, col);
                }
            }

            Punto inicio = leerPunto(lector, filas, columnas, "de inicio");
            Punto destino = leerPunto(lector, filas, columnas, "de destino");

            casos.add(new MisionUnoCaso(grid, inicio, destino));
        }

        // Si sobran tokens despues del sentinela R=0 C=0, casi siempre significa
        // que alguna "cantidad de bombas" vino mal contada. Se avisa en vez de
        // ignorarlo, tal como exige la seccion 2.2.
        if (lector.haySiguiente()) {
            throw new EEntradaInvalida("Sobran datos despues del caso de fin de entrada (R=0, C=0). "
                    + "Revise que cada fila con bombas coincida con lo declarado.");
        }

        return casos;
    }
    // Lee fila y columna de un punto (inicio o destino) y valida que caiga dentro de la grilla.
    private static Punto leerPunto(LectorTokens lector, int filas, int columnas, String etiqueta)
            throws EEntradaInvalida {

        int fila = lector.siguienteEntero("la fila " + etiqueta);
        int col = lector.siguienteEntero("la columna " + etiqueta);
        validarCoordenada(fila, filas, "fila " + etiqueta);
        validarCoordenada(col, columnas, "columna " + etiqueta);
        return new Punto(fila, col);
    }

    // Valida que un indice (fila o columna) este dentro de 0..limite-1.
    private static void validarCoordenada(int valor, int limite, String descripcion) throws EEntradaInvalida {
        if (valor < 0 || valor >= limite) {
            throw new EEntradaInvalida("Valor de " + descripcion + " fuera de rango: " + valor
                    + " (debe estar entre 0 y " + (limite - 1) + ")");
        }
    }

    public static final String EJEMPLO_MISSION2 =
            "3\n" +
                    "2 1 0 1\n" +
                    "0 1 100\n" +
                    "3 3 2 0\n" +
                    "0 1 100\n" +
                    "0 2 200\n" +
                    "1 2 50\n" +
                    "2 0 0 1\n";

    public static List<MisionDosCaso> leerMision2(String texto) throws EEntradaInvalida, ENumeroNegativo {
        LectorTokens lector = new LectorTokens(texto);
        int casos = lector.siguienteEntero("la cantidad de casos de prueba (T)");

        if (casos < 0) {
            throw new EEntradaInvalida("La cantidad de casos de prueba no puede ser negativa: " + casos);
        }

        List<MisionDosCaso> listaCasos = new ArrayList<>();

        for (int caso = 1; caso <= casos; caso++) {
            int nodos = lector.siguienteEntero("N (cantidad de nodos) del caso " + caso);
            if (nodos < 1 || nodos > 10000) {
                throw new EEntradaInvalida("N debe estar entre 1 y 10000 en el caso " + caso + ", se leyo: " + nodos);
            }

            int conexiones = lector.siguienteEntero("C (cantidad de conexiones) del caso " + caso);
            if (conexiones < 0 || conexiones > 100000) {
                throw new EEntradaInvalida("C debe estar entre 0 y 100000 en el caso " + caso + ", se leyo: " + conexiones);
            }

            int origen = lector.siguienteEntero("S (nodo origen) del caso " + caso);
            int destino = lector.siguienteEntero("D (nodo destino) del caso " + caso);

            validarNodoMision2(origen, nodos, "origen (S)", caso);
            validarNodoMision2(destino, nodos, "destino (D)", caso);

            Graph grafo = new Graph(nodos, false); // Grafo NO dirigido

            for (int i = 1; i <= conexiones; i++) {
                int desde = lector.siguienteEntero("el nodo A de la conexion " + i + " (caso " + caso + ")");
                int hasta = lector.siguienteEntero("el nodo B de la conexion " + i + " (caso " + caso + ")");
                long costo = lector.siguienteLargo("el costo W de la conexion " + i + " (caso " + caso + ")");

                validarNodoMision2(desde, nodos, "de la conexion " + i, caso);
                validarNodoMision2(hasta, nodos, "de la conexion " + i, caso);

                if (costo < 0 || costo > 1000000) {
                    throw new EEntradaInvalida("El costo W debe estar entre 0 y 1000000 en la conexion " + i);
                }

                // Los nodos ya estan en base 0 (0 a N-1), no se les resta nada
                grafo.addEdge(desde, hasta, costo);
            }

            listaCasos.add(new MisionDosCaso(grafo, origen, destino));
        }

        if (lector.haySiguiente()) {
            throw new EEntradaInvalida("Sobran datos despues del ultimo caso de prueba.");
        }

        return listaCasos;
    }

    private static void validarNodoMision2(int nodo, int totalNodos, String etiqueta, int caso) throws EEntradaInvalida {
        if (nodo < 0 || nodo >= totalNodos) {
            throw new EEntradaInvalida("El nodo " + etiqueta + " es " + nodo + ", pero debe estar entre 0 y " + (totalNodos - 1) + " (caso " + caso + ")");
        }
    }

    // Entrada de ejemplo de la mision 3, tal cual aparece en el enunciado (seccion 5).
    public static final String EJEMPLO_MISSION3 =
            "3\n" +
                    "5 7 0 4\n" +
                    "0 1 50\n" +
                    "0 2 10\n" +
                    "1 2 -30\n" +
                    "1 3 40\n" +
                    "2 1 -5\n" +
                    "2 3 60\n" +
                    "3 4 20\n" +
                    "4 4 0 3\n" +
                    "0 1 20\n" +
                    "1 2 30\n" +
                    "2 1 -10\n" +
                    "2 3 15\n" +
                    "3 3 0 2\n" +
                    "0 1 -40\n" +
                    "1 2 -25\n" +
                    "0 2 -80\n";

    /*
        MISION 3 (Floyd-Warshall & Bellman-Ford - La Reserva de Churun).

        Formato esperado:
            T                           -> cantidad de casos de prueba
            para cada caso:
                N M S D                 -> nodos, pasajes, origen, destino
                M veces: A B W          -> pasaje DIRIGIDO de A a B que rinde W churun

        A diferencia de las Misiones 2 y 4, aqui el grafo es DIRIGIDO
        (new Graph(N, true)): un pasaje A B no se puede recorrer de B a A.
        Los pesos SI pueden ser negativos (-1000 <= W <= 1000), porque
        representan churun ganado o perdido en el pasaje, no una distancia.

        Los nodos ya vienen numerados de 0 a N-1 en el enunciado (a diferencia
        de la Mision 4, que numera desde 1), asi que no hace falta ninguna
        conversion de indices aqui.

        Pasajes repetidos entre el mismo par ordenado se agregan TODOS, sin
        quedarse con uno solo (seccion 5: "treat each one as a separate edge");
        Graph.addEdge ya soporta aristas duplicadas sin filtrarlas, e incluso
        auto-ciclos (A == B), que son justamente los que Floyd-Warshall usa
        para detectar un ciclo de ganancia positiva de un solo nodo.
    */
    public static List<MisionTresCaso> leerMision3(String texto) throws EEntradaInvalida, ENumeroNegativo {
        LectorTokens lector = new LectorTokens(texto);
        int casos = lector.siguienteEntero("la cantidad de casos de prueba (T)");

        if (casos < 0) {
            throw new EEntradaInvalida("La cantidad de casos de prueba no puede ser negativa: " + casos);
        }

        List<MisionTresCaso> listaCasos = new ArrayList<>();

        for (int caso = 1; caso <= casos; caso++) {
            int nodos = lector.siguienteEntero("N (cantidad de nodos) del caso " + caso);
            if (nodos < 1 || nodos > 100) {
                throw new EEntradaInvalida("N debe estar entre 1 y 100 en el caso " + caso + ", se leyo: " + nodos);
            }

            int pasajes = lector.siguienteEntero("M (cantidad de pasajes) del caso " + caso);
            if (pasajes < 0 || pasajes > 5000) {
                throw new EEntradaInvalida("M debe estar entre 0 y 5000 en el caso " + caso + ", se leyo: " + pasajes);
            }

            int origen = lector.siguienteEntero("S (nodo origen) del caso " + caso);
            int destino = lector.siguienteEntero("D (nodo destino) del caso " + caso);

            validarNodoMision3(origen, nodos, "origen (S)", caso);
            validarNodoMision3(destino, nodos, "destino (D)", caso);

            // Grafo DIRIGIDO: el 'true' es clave. Con 'false' cada pasaje se
            // podria recorrer tambien al reves, lo que rompe todo el sentido
            // de la deteccion de ciclos de esta mision.
            Graph grafo = new Graph(nodos, true);

            for (int i = 1; i <= pasajes; i++) {
                int desde = lector.siguienteEntero("el nodo A del pasaje " + i + " (caso " + caso + ")");
                int hasta = lector.siguienteEntero("el nodo B del pasaje " + i + " (caso " + caso + ")");
                long churun = lector.siguienteLargo("el churun W del pasaje " + i + " (caso " + caso + ")");

                validarNodoMision3(desde, nodos, "A del pasaje " + i, caso);
                validarNodoMision3(hasta, nodos, "B del pasaje " + i, caso);

                // A diferencia de las Misiones 2 y 4, aqui el peso SI puede
                // ser negativo (seccion 5: -1000 <= W <= 1000).
                if (churun < -1000 || churun > 1000) {
                    throw new EEntradaInvalida("El churun W debe estar entre -1000 y 1000 en el pasaje " + i
                            + " (caso " + caso + "), se leyo: " + churun);
                }

                grafo.addEdge(desde, hasta, churun);
            }

            listaCasos.add(new MisionTresCaso(grafo, origen, destino));
        }

        if (lector.haySiguiente()) {
            throw new EEntradaInvalida("Sobran datos despues del ultimo caso de prueba.");
        }

        return listaCasos;
    }

    private static void validarNodoMision3(int nodo, int totalNodos, String etiqueta, int caso) throws EEntradaInvalida {
        if (nodo < 0 || nodo >= totalNodos) {
            throw new EEntradaInvalida("El nodo " + etiqueta + " es " + nodo + ", pero debe estar entre 0 y " + (totalNodos - 1) + " (caso " + caso + ")");
        }
    }

    /*
        MISION 4 (Kruskal).

        Formato esperado:
            T                          -> cantidad de casos de prueba
            para cada caso:
                N                      -> cantidad de intersecciones (1..10000)
                C                      -> cantidad de cables (0..100000)
                C veces: A B costo     -> cable bidireccional entre A y B

        Devuelve un Graph NO DIRIGIDO por cada caso, ya listo para Kruskal.

        Ojo con la conversion de indices: el enunciado numera las intersecciones
        desde 1 hasta N, pero el modelo Graph trabaja desde 0 hasta N-1.
        La resta de 1 se hace aqui y en ningun otro lado, para que el algoritmo
        no tenga que saber nada de como venia numerada la entrada.
    */
    public static List<Graph> leerMission4(String texto) throws EEntradaInvalida, ENumeroNegativo {

        // El lector parte el texto en tokens y lleva la cuenta de por donde va.
        LectorTokens lector = new LectorTokens(texto);

        // Primer token del archivo: cuantos casos vienen.
        int casos = lector.siguienteEntero("la cantidad de casos de prueba (T)");

        // Un numero negativo de casos no tiene sentido.
        if (casos < 0) {
            throw new EEntradaInvalida("La cantidad de casos de prueba no puede ser negativa: " + casos);
        }

        // Aqui se va acumulando un grafo por cada caso leido.
        List<Graph> grafos = new ArrayList<>();

        for (int caso = 1; caso <= casos; caso++) {

            // N: cuantas intersecciones tiene la red de este caso.
            int nodos = lector.siguienteEntero("N (cantidad de intersecciones) del caso " + caso);

            // El enunciado garantiza 1 <= N <= 10000. Si llega otra cosa, es entrada corrupta.
            if (nodos < 1 || nodos > 10000) {
                throw new EEntradaInvalida("N debe estar entre 1 y 10000 en el caso " + caso + ", pero se leyo: " + nodos);
            }

            // C: cuantos cables disponibles hay. Puede ser 0 (red sin ningun cable),
            // pero nunca mas de 100000, segun el enunciado.
            int cables = lector.siguienteEntero("C (cantidad de cables) del caso " + caso);

            if (cables < 0 || cables > 100000) {
                throw new EEntradaInvalida("C debe estar entre 0 y 100000 en el caso " + caso + ", pero se leyo: " + cables);
            }

            // Grafo NO dirigido: un cable se puede recorrer en los dos sentidos.
            // El false es clave; con true Kruskal contaria cada cable dos veces.
            Graph grafo = new Graph(nodos, false);

            for (int i = 1; i <= cables; i++) {

                // Los tres tokens de un cable se leen seguidos, sin importar
                // si el usuario los puso en una linea o repartidos en varias.
                int desde = lector.siguienteEntero("la interseccion de origen del cable " + i + " (caso " + caso + ")");
                int hasta = lector.siguienteEntero("la interseccion de destino del cable " + i + " (caso " + caso + ")");
                long costo = lector.siguienteLargo("el costo del cable " + i + " (caso " + caso + ")");

                // Las intersecciones deben caer dentro de 1..N.
                validarInterseccion(desde, nodos, i, caso);
                validarInterseccion(hasta, nodos, i, caso);

                // En la mision 4 los costos son no negativos (0 <= costo <= 1.000.000).
                if (costo < 0 || costo > 1000000) {
                    throw new EEntradaInvalida("El costo del cable " + i + " (caso " + caso
                            + ") debe estar entre 0 y 1000000: " + costo);
                }

                // Aqui ocurre la traduccion 1..N -> 0..N-1.
                // Los cables repetidos y los auto-ciclos (desde == hasta) se
                // agregan sin filtrar: el enunciado avisa que pueden venir y
                // el union-find de Kruskal los descarta solo.
                grafo.addEdge(desde - 1, hasta - 1, costo);
            }

            grafos.add(grafo);
        }

        // Si despues del ultimo caso todavia quedan tokens, casi siempre
        // significa que T o algun C venian mal contados. Avisamos en vez de
        // ignorarlo: el enunciado prohibe explicitamente el fallo silencioso.
        if (lector.haySiguiente()) {
            throw new EEntradaInvalida("Sobran datos despues del ultimo caso. "
                    + "Revise que T y cada C coincidan con la cantidad de lineas pegadas.");
        }

        return grafos;
    }

    // Comprueba que una interseccion este dentro del rango 1..N que define el enunciado.
    private static void validarInterseccion(int interseccion, int nodos, int numeroCable, int caso)
            throws EEntradaInvalida {

        if (interseccion < 1 || interseccion > nodos) {
            throw new EEntradaInvalida("La interseccion " + interseccion + " del cable " + numeroCable
                    + " (caso " + caso + ") esta fuera del rango valido 1.." + nodos);
        }
    }

    /*
        Lector de tokens: recibe todo el texto pegado y lo entrega de a un
        token a la vez. Es lo que permite cumplir la seccion 2.2 sin esfuerzo,
        porque el formato de las lineas deja de importar por completo.

        Ademas lleva el indice del token actual, y gracias a eso los mensajes
        de error pueden decir exactamente en que posicion se rompio la entrada.
    */
    private static final class LectorTokens {

        // Todos los tokens del texto, ya separados.
        private final String[] tokens;

        // Cual es el proximo token por consumir.
        private int indice;

        private LectorTokens(String texto) throws EEntradaInvalida {

            // Una entrada nula o en blanco es un error de usuario, no un crash.
            if (texto == null || texto.trim().isEmpty()) {
                throw new EEntradaInvalida("La entrada esta vacia. Pegue los datos o use el boton de ejemplo.");
            }

            // trim() quita espacios y saltos al inicio y al final.
            // split("\\s+") corta por CUALQUIER bloque de espacios, tabulaciones
            // o saltos de linea, asi que las lineas en blanco desaparecen solas.
            this.tokens = texto.trim().split("\\s+");
            this.indice = 0;
        }

        // Quedan tokens por leer?
        private boolean haySiguiente() {
            return indice < tokens.length;
        }

        // Consume el proximo token crudo. Si no hay, la entrada quedo incompleta.
        private String siguiente(String queSeEsperaba) throws EEntradaInvalida {

            if (!haySiguiente()) {
                throw new EEntradaInvalida("La entrada termino antes de tiempo: falta " + queSeEsperaba + ".");
            }

            // Se devuelve el token actual y se avanza el cursor.
            return tokens[indice++];
        }

        // Consume el proximo token y lo interpreta como int.
        private int siguienteEntero(String queSeEsperaba) throws EEntradaInvalida {

            String token = siguiente(queSeEsperaba);

            try {
                return Integer.parseInt(token);
            } catch (NumberFormatException e) {
                // Se convierte el error tecnico de Java en un mensaje que el usuario entiende.
                throw new EEntradaInvalida("Se esperaba " + queSeEsperaba + " (un numero entero) en la posicion "
                        + indice + ", pero se encontro: \"" + token + "\"");
            }
        }

        // Igual que el anterior pero para long, que es lo que usan los costos.
        private long siguienteLargo(String queSeEsperaba) throws EEntradaInvalida {

            String token = siguiente(queSeEsperaba);

            try {
                return Long.parseLong(token);
            } catch (NumberFormatException e) {
                throw new EEntradaInvalida("Se esperaba " + queSeEsperaba + " (un numero entero) en la posicion "
                        + indice + ", pero se encontro: \"" + token + "\"");
            }
        }
    }
}