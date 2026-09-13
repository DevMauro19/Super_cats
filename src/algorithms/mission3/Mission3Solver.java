package algorithms.mission3;

import Exceptions.EFueraRango;
import model.Graph;

/*
    Orquestador de la Mision 3: para un caso (grafo, S, D) corre Floyd-Warshall
    y Bellman-Ford de forma COMPLETAMENTE INDEPENDIENTE (cada uno con su propio
    algoritmo y su propia deteccion de ciclos), y despues compara sus dos
    conclusiones para el par (S, D).

    Por que hace falta esto y no basta con uno de los dos algoritmos:
        - Floyd-Warshall es la fuente oficial de la respuesta impresa (seccion
          5, punto 1: "The printed answer for the test case is the entry at
          row S, column D").
        - Bellman-Ford se exige ADEMAS como verificacion cruzada (seccion 5,
          punto 2: "Its result for node D must agree with the Floyd-Warshall
          matrix; the GUI must report a mismatch if the two ever disagree").

    Esta clase es el unico lugar que sabe que ambos algoritmos deben coincidir;
    ni FloydWarshall ni BellmanFord se conocen entre si.
*/
public final class Mission3Solver {

    private Mission3Solver() {
    }

    public static Mission3Result resolver(Graph grafo, int s, int d) throws EFueraRango {

        grafo.NodoValido(s);
        grafo.NodoValido(d);

        FloydWarshallResult fw = FloydWarshall.resolver(grafo);
        BellmanFordResult bf = BellmanFord.resolver(grafo, s);

        boolean fwBloqueado = !fw.esAlcanzable(s, d);
        boolean fwInfinito = !fwBloqueado && fw.esNoAcotado(s, d);
        long fwValor = fwBloqueado ? 0 : fw.getValor(s, d);

        boolean bfBloqueado = !bf.esAlcanzable(d);
        boolean bfInfinito = !bfBloqueado && bf.esNoAcotado(d);
        long bfValor = bfBloqueado ? 0 : bf.getDistancia(d);

        return compararResultados(fwBloqueado, fwInfinito, fwValor, bfBloqueado, bfInfinito, bfValor);
    }

    /*
        Compara las dos conclusiones (una por algoritmo) y arma el resultado
        final, aplicando la precedencia de la seccion 5 usando SIEMPRE los
        datos de Floyd-Warshall como fuente de la respuesta impresa.

        Se deja como metodo publico y separado de resolver() (en vez de
        enterrarlo ahi adentro) justamente para poder alimentarlo con valores
        fabricados a mano desde un test y comprobar que la deteccion de
        mismatch SI dispara cuando dos resultados disienten, sin depender de
        encontrar (o fabricar) un grafo real donde los algoritmos se
        equivoquen entre si.
    */
    public static Mission3Result compararResultados(
            boolean fwBloqueado, boolean fwInfinito, long fwValor,
            boolean bfBloqueado, boolean bfInfinito, long bfValor) {

        boolean mismatch;
        String detalle = null;

        if (fwBloqueado != bfBloqueado) {
            mismatch = true;
            detalle = "Floyd-Warshall dice alcanzable=" + !fwBloqueado
                    + " pero Bellman-Ford dice alcanzable=" + !bfBloqueado;

        } else if (!fwBloqueado && fwInfinito != bfInfinito) {
            mismatch = true;
            detalle = "Floyd-Warshall dice no-acotado=" + fwInfinito
                    + " pero Bellman-Ford dice no-acotado=" + bfInfinito;

        } else if (!fwBloqueado && !fwInfinito && fwValor != bfValor) {
            mismatch = true;
            detalle = "Floyd-Warshall calculo " + fwValor + " pero Bellman-Ford calculo " + bfValor;

        } else {
            mismatch = false;
        }

        if (fwBloqueado) {
            return Mission3Result.bloqueado(mismatch, detalle);
        }
        if (fwInfinito) {
            return Mission3Result.infinito(mismatch, detalle);
        }
        return Mission3Result.valor(fwValor, mismatch, detalle);
    }
}
