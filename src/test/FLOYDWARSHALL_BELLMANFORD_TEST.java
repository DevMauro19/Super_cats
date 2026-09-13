package test;

import Exceptions.EEntradaInvalida;
import Exceptions.ENumeroNegativo;
import algorithms.mission3.Mission3Result;
import algorithms.mission3.Mission3Solver;
import io.Input;
import io.MisionTresCaso;
import io.Output;

import java.util.List;

/**
 * Prueba manual que reproduce EXACTAMENTE el Sample Input/Output del PDF para la Misión 3:
 *
 * Case #1: 110
 * Case #2: Infinite churun!
 * Case #3: -65
 *
 * Ademas verifica, en cada caso, que Floyd-Warshall y Bellman-Ford NUNCA
 * reporten un mismatch sobre el ejemplo del enunciado, y ejercita por separado
 * (con valores fabricados a mano) que la deteccion de mismatch SI dispara
 * cuando dos resultados disienten — el checklist final (seccion 10) pide que
 * esa advertencia se haya probado al menos una vez.
 */
public final class FLOYDWARSHALL_BELLMANFORD_TEST {

    public static void main(String[] args) {
        boolean allPassed = true;
        try {
            allPassed &= ejemploDelEnunciado();
            allPassed &= deteccionDeMismatch();
        } catch (EEntradaInvalida | ENumeroNegativo e) {
            throw new AssertionError("La prueba fallo por error de entrada o modelo", e);
        }

        System.out.println(allPassed ? "PASS" : "FAIL");
        if (!allPassed) {
            System.exit(1);
        }
    }

    private static boolean ejemploDelEnunciado() throws EEntradaInvalida, ENumeroNegativo {

        List<MisionTresCaso> casos = Input.leerMision3(Input.EJEMPLO_MISSION3);

        String[] esperados = {
                "Case #1: 110",
                "Case #2: Infinite churun!",
                "Case #3: -65"
        };

        boolean allPassed = true;

        for (int i = 0; i < casos.size(); i++) {
            MisionTresCaso caso = casos.get(i);
            Mission3Result res = Mission3Solver.resolver(caso.getGrafo(), caso.getOrigen(), caso.getDestino());
            String obtenido = Output.formatearMision3(i + 1, res);

            System.out.println("Obtenido: " + obtenido + " | Esperado: " + esperados[i]);

            if (res.hayMismatch()) {
                System.out.println("  MISMATCH inesperado Floyd-Warshall vs Bellman-Ford: " + res.getDetalleMismatch());
                allPassed = false;
            }

            if (!obtenido.equals(esperados[i])) {
                allPassed = false;
            }
        }

        return allPassed;
    }

    /*
        Mission3Solver.compararResultados() es la unica pieza responsable de
        detectar el mismatch (seccion 5, punto 2). Se la alimenta aqui con
        conclusiones fabricadas a mano que DEBEN disentir, sin depender de
        encontrar un grafo real donde ambos algoritmos correctos se
        contradigan entre si (si estan bien implementados, nunca deberian).
    */
    private static boolean deteccionDeMismatch() {

        boolean allPassed = true;

        // Caso 1: un algoritmo dice "bloqueado" y el otro dice "alcanzable".
        Mission3Result r1 = Mission3Solver.compararResultados(
                true, false, 0,
                false, false, 42);
        if (!r1.hayMismatch()) {
            System.out.println("FALLO: deberia detectar mismatch de alcanzabilidad");
            allPassed = false;
        } else {
            System.out.println("OK: mismatch de alcanzabilidad detectado -> " + r1.getDetalleMismatch());
        }

        // Caso 2: ambos dicen "alcanzable", pero solo uno detecta el ciclo infinito.
        Mission3Result r2 = Mission3Solver.compararResultados(
                false, true, 0,
                false, false, 100);
        if (!r2.hayMismatch()) {
            System.out.println("FALLO: deberia detectar mismatch de no-acotado");
            allPassed = false;
        } else {
            System.out.println("OK: mismatch de no-acotado detectado -> " + r2.getDetalleMismatch());
        }

        // Caso 3: ambos coinciden en que es finito, pero calculan numeros distintos.
        Mission3Result r3 = Mission3Solver.compararResultados(
                false, false, 110,
                false, false, 99);
        if (!r3.hayMismatch()) {
            System.out.println("FALLO: deberia detectar mismatch de valor");
            allPassed = false;
        } else {
            System.out.println("OK: mismatch de valor detectado -> " + r3.getDetalleMismatch());
        }

        // Caso de control: dos resultados identicos NUNCA deben marcar mismatch.
        Mission3Result r4 = Mission3Solver.compararResultados(
                false, false, -65,
                false, false, -65);
        if (r4.hayMismatch()) {
            System.out.println("FALLO: no deberia marcar mismatch cuando ambos coinciden");
            allPassed = false;
        } else {
            System.out.println("OK: sin mismatch cuando ambos algoritmos coinciden");
        }

        return allPassed;
    }
}
