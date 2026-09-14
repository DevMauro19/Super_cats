package test;

import Exceptions.EEntradaInvalida;
import Exceptions.EFueraRango;
import Exceptions.ENumeroNegativo;
import algorithms.mission4.Kruskal;
import algorithms.mission4.Union;
import io.Input;
import io.Output;
import model.Graph;

import java.util.List;

/**
 * Prueba manual que reproduce EXACTAMENTE el Sample Input/Output del PDF para la Misión 4:
 *
 * Case #1: 55
 *
 * Ademas cubre, con entradas fabricadas a mano, los casos limite que el
 * enunciado exige que la GUI maneje sin fallo silencioso: una red que no se
 * puede reconectar (mensaje "Limon cut too many cables"), un solo nodo,
 * cables duplicados y auto-ciclos, y las validaciones de rango de
 * Input.leerMission4 (incluidos los limites superiores de N, C y costo).
 *
 * Por separado se ejercita Union como estructura independiente: union por
 * tamano + compresion de caminos, uniones repetidas, auto-ciclos y los
 * errores de rango/cantidad negativa — el checklist final (seccion 10) pide
 * que "path compression" y "union by size" queden probados explicitamente.
 */
public final class KRUSKAL_TEST {

    public static void main(String[] args) {
        boolean allPassed = true;
        try {
            allPassed &= ejemploDelEnunciado();
            allPassed &= redDesconectada();
            allPassed &= unSoloNodo();
            allPassed &= cablesDuplicadosYAutociclos();
            allPassed &= validacionesDeEntrada();
        } catch (EEntradaInvalida | ENumeroNegativo e) {
            throw new AssertionError("La prueba fallo por error de entrada o modelo", e);
        }

        try {
            allPassed &= unionFindSanityChecks();
        } catch (ENumeroNegativo e) {
            throw new AssertionError("La prueba de Union fallo por un error inesperado", e);
        }

        System.out.println(allPassed ? "PASS" : "FAIL");
        if (!allPassed) {
            System.exit(1);
        }
    }

    /*
        Exactamente el ejemplo de la seccion 6 del enunciado, leido a traves
        del parser real (Input.leerMission4 + Input.EJEMPLO_MISSION4), tal
        como lo dispararia el boton "load sample" de la GUI.
    */
    private static boolean ejemploDelEnunciado() throws EEntradaInvalida, ENumeroNegativo {

        List<Graph> grafos = Input.leerMission4(Input.EJEMPLO_MISSION4);

        String[] esperados = {
                "Case #1: 55"
        };

        boolean allPassed = true;

        for (int i = 0; i < grafos.size(); i++) {
            Kruskal.Resultado resultado = Kruskal.ejecutar(grafos.get(i));
            String obtenido = Output.formatearMision4(i + 1, resultado);

            System.out.println("Obtenido: " + obtenido + " | Esperado: " + esperados[i]);

            if (!obtenido.equals(esperados[i])) {
                allPassed = false;
            }
        }

        return allPassed;
    }

    /*
        Red que NO se puede reconectar: dos parejas de intersecciones
        aisladas entre si (1-2 por un lado, 3-4 por el otro, sin nada que las
        una). Debe imprimirse el mensaje exacto del enunciado.
    */
    private static boolean redDesconectada() throws EEntradaInvalida, ENumeroNegativo {

        String entrada = "1\n4\n2\n1 2 5\n3 4 7\n";
        List<Graph> grafos = Input.leerMission4(entrada);

        Kruskal.Resultado resultado = Kruskal.ejecutar(grafos.get(0));
        String obtenido = Output.formatearMision4(1, resultado);
        String esperado = "Case #1: Limon cut too many cables";

        System.out.println("Obtenido: " + obtenido + " | Esperado: " + esperado);

        boolean allPassed = obtenido.equals(esperado);

        if (resultado.isConectado()) {
            System.out.println("FALLO: el resultado se marco como conectado y no deberia");
            allPassed = false;
        }

        return allPassed;
    }

    /*
        Caso especial N=1: no hace falta ningun cable, la respuesta es 0,
        incluso si llega un cable de sobra (auto-ciclo en la interseccion 1).
    */
    private static boolean unSoloNodo() throws EEntradaInvalida, ENumeroNegativo {

        String entrada = "1\n1\n1\n1 1 999\n";
        List<Graph> grafos = Input.leerMission4(entrada);

        Kruskal.Resultado resultado = Kruskal.ejecutar(grafos.get(0));
        String obtenido = Output.formatearMision4(1, resultado);
        String esperado = "Case #1: 0";

        System.out.println("Obtenido: " + obtenido + " | Esperado: " + esperado);

        boolean allPassed = obtenido.equals(esperado);

        if (!resultado.getCablesSeleccionados().isEmpty()) {
            System.out.println("FALLO: con un solo nodo no deberia seleccionarse ningun cable");
            allPassed = false;
        }

        return allPassed;
    }

    /*
        Cables duplicados entre el mismo par y un auto-ciclo: el auto-ciclo
        jamas puede entrar al MST (Union.unir(a,a) siempre da false), y entre
        los dos cables 1-2 debe quedar el mas barato (3), no el de costo 5.
        Costo esperado del MST: 3 (1-2) + 7 (2-3) = 10.
    */
    private static boolean cablesDuplicadosYAutociclos() throws EEntradaInvalida, ENumeroNegativo {

        String entrada = "1\n3\n4\n"
                + "1 2 5\n"   // mas caro entre 1-2
                + "1 2 3\n"   // mas barato entre 1-2 (deberia quedar este)
                + "2 2 100\n" // auto-ciclo: nunca entra al MST
                + "2 3 7\n";

        List<Graph> grafos = Input.leerMission4(entrada);
        Kruskal.Resultado resultado = Kruskal.ejecutar(grafos.get(0));
        String obtenido = Output.formatearMision4(1, resultado);
        String esperado = "Case #1: 10";

        System.out.println("Obtenido: " + obtenido + " | Esperado: " + esperado);

        boolean allPassed = obtenido.equals(esperado);

        boolean usaElAutociclo = resultado.getCablesSeleccionados().stream()
                .anyMatch(c -> c.getFrom() == c.getTo());
        if (usaElAutociclo) {
            System.out.println("FALLO: un auto-ciclo entro al MST");
            allPassed = false;
        } else {
            System.out.println("OK: el auto-ciclo fue descartado correctamente");
        }

        boolean usaElCableMasCaro = resultado.getCablesSeleccionados().stream()
                .anyMatch(c -> c.getWeight() == 5);
        if (usaElCableMasCaro) {
            System.out.println("FALLO: quedo seleccionado el cable duplicado mas caro (5) en vez del barato (3)");
            allPassed = false;
        } else {
            System.out.println("OK: entre los cables duplicados quedo el mas barato");
        }

        return allPassed;
    }

    /*
        Union-Find aislado del resto: union por tamano, compresion de
        caminos, uniones repetidas, auto-ciclos y los dos errores que exige
        el enunciado (rango invalido, cantidad negativa de elementos).
    */
    private static boolean unionFindSanityChecks() throws ENumeroNegativo {

        boolean allPassed = true;

        Union u = new Union(4);
        if (u.getComponentes() != 4) {
            System.out.println("FALLO: Union(4) deberia arrancar con 4 componentes, tiene " + u.getComponentes());
            allPassed = false;
        } else {
            System.out.println("OK: Union(4) arranca con 4 componentes separados");
        }

        if (!u.unir(0, 1) || !u.unir(1, 2) || !u.unir(2, 3)) {
            System.out.println("FALLO: las tres uniones deberian ser efectivas (elementos antes separados)");
            allPassed = false;
        }

        if (u.getComponentes() != 1 || !u.estanConectados(0, 3) || u.buscar(0) != u.buscar(3)) {
            System.out.println("FALLO: tras 3 uniones en cadena, los 4 elementos deberian quedar en un solo clan");
            allPassed = false;
        } else {
            System.out.println("OK: union por tamano + compresion de caminos dejan a 0 y 3 en el mismo clan");
        }

        if (u.unir(0, 3)) {
            System.out.println("FALLO: unir dos elementos ya conectados deberia devolver false");
            allPassed = false;
        } else {
            System.out.println("OK: unir elementos ya conectados devuelve false (cerraria un ciclo)");
        }

        Union soloAutociclo = new Union(2);
        if (soloAutociclo.unir(1, 1)) {
            System.out.println("FALLO: unir un elemento consigo mismo deberia devolver false");
            allPassed = false;
        } else {
            System.out.println("OK: unir un elemento consigo mismo devuelve false y no cambia componentes");
        }

        try {
            u.buscar(-1);
            System.out.println("FALLO: buscar(-1) deberia lanzar EFueraRango");
            allPassed = false;
        } catch (EFueraRango e) {
            System.out.println("OK: buscar(-1) lanza EFueraRango");
        }

        try {
            u.buscar(4); // Union(4) valido es 0..3
            System.out.println("FALLO: buscar(4) deberia lanzar EFueraRango");
            allPassed = false;
        } catch (EFueraRango e) {
            System.out.println("OK: buscar(4) lanza EFueraRango");
        }

        try {
            new Union(-1);
            System.out.println("FALLO: Union(-1) deberia lanzar ENumeroNegativo");
            allPassed = false;
        } catch (ENumeroNegativo e) {
            System.out.println("OK: Union(-1) lanza ENumeroNegativo");
        }

        return allPassed;
    }

    /*
        Input.leerMission4 debe rechazar entrada malformada con un mensaje
        legible (seccion 2.2), nunca un stack trace ni un fallo silencioso.
        Se cubren especialmente los tres limites superiores que faltaban
        (N <= 10000, C <= 100000, costo <= 1000000).
    */
    private static boolean validacionesDeEntrada() {

        boolean allPassed = true;

        allPassed &= esperaEEntradaInvalida("N > 10000", "1\n10001\n0\n");
        allPassed &= esperaEEntradaInvalida("C > 100000", "1\n2\n100001\n");
        allPassed &= esperaEEntradaInvalida("costo > 1000000", "1\n2\n1\n1 2 1000001\n");
        allPassed &= esperaEEntradaInvalida("costo negativo", "1\n2\n1\n1 2 -5\n");
        allPassed &= esperaEEntradaInvalida("N = 0", "1\n0\n0\n");
        allPassed &= esperaEEntradaInvalida("interseccion 0 (fuera de 1..N)", "1\n3\n1\n0 2 5\n");
        allPassed &= esperaEEntradaInvalida("interseccion mayor a N", "1\n3\n1\n1 4 5\n");
        allPassed &= esperaEEntradaInvalida("sobran tokens al final", "1\n3\n1\n1 2 5\n2 3 6\n");
        allPassed &= esperaEEntradaInvalida("entrada vacia", "");

        return allPassed;
    }

    // Corre leerMission4 sobre 'entrada' y verifica que lance EEntradaInvalida.
    private static boolean esperaEEntradaInvalida(String descripcion, String entrada) {
        try {
            Input.leerMission4(entrada);
            System.out.println("FALLO: se esperaba EEntradaInvalida para: " + descripcion);
            return false;
        } catch (EEntradaInvalida e) {
            System.out.println("OK: EEntradaInvalida detectada para: " + descripcion);
            return true;
        } catch (ENumeroNegativo e) {
            System.out.println("FALLO: se esperaba EEntradaInvalida pero se lanzo ENumeroNegativo para: " + descripcion);
            return false;
        }
    }
}