package test;

import Exceptions.EEntradaInvalida;
import Exceptions.ENumeroNegativo;
import algorithms.mission2.Dijkstra;
import algorithms.mission2.DijkstraResult;
import io.Input;
import io.MisionDosCaso;
import io.Output;

import java.util.List;

/**
 * Prueba manual que reproduce EXACTAMENTE el Sample Input/Output del PDF para la Misión 2:
 *
 * Case #1: 100
 * Case #2: 150
 * Case #3: Nina is very sad
 */
public final class DIJKSTRA_TEST {

    public static void main(String[] args) {
        try {
            List<MisionDosCaso> casos = Input.leerMision2(Input.EJEMPLO_MISSION2);

            String[] esperados = {
                    "Case #1: 100",
                    "Case #2: 150",
                    "Case #3: Nina is very sad"
            };

            boolean allPassed = true;

            for (int i = 0; i < casos.size(); i++) {
                MisionDosCaso caso = casos.get(i);
                DijkstraResult res = Dijkstra.resolver(caso.getGrafo(), caso.getOrigen(), caso.getDestino());
                String obtenido = Output.formatearMision2(i + 1, res);

                System.out.println("Obtenido: " + obtenido + " | Esperado: " + esperados[i]);

                if (!obtenido.equals(esperados[i])) {
                    allPassed = false;
                }
            }

            System.out.println(allPassed ? "PASS" : "FAIL");
            if (!allPassed) {
                System.exit(1);
            }

        } catch (EEntradaInvalida | ENumeroNegativo e) {
            throw new AssertionError("La prueba fallo por error de entrada o modelo", e);
        }
    }
}