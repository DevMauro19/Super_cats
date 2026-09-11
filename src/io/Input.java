package io;

import Exceptions.EEntradaInvalida;
import Exceptions.ENumeroNegativo;
import model.Graph;

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

            // El enunciado garantiza N >= 1. Si llega otra cosa, es entrada corrupta.
            if (nodos < 1) {
                throw new EEntradaInvalida("N debe ser al menos 1 en el caso " + caso + ", pero se leyo: " + nodos);
            }

            // C: cuantos cables disponibles hay. Puede ser 0 (red sin ningun cable).
            int cables = lector.siguienteEntero("C (cantidad de cables) del caso " + caso);

            if (cables < 0) {
                throw new EEntradaInvalida("C no puede ser negativo en el caso " + caso + ", pero se leyo: " + cables);
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
                if (costo < 0) {
                    throw new EEntradaInvalida("El costo del cable " + i + " (caso " + caso
                            + ") no puede ser negativo: " + costo);
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