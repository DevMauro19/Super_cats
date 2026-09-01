package model;

/*
    Arista dirigida almacenada dentro de la lista de adyacencia de un Graph.
    Un objeto Edge guardado un adjavencia[deDonde] representa "deDonde -> para" con el
    peso indicado. Para grafos no dirigidos, Graph se encarga de insertar dos
    Edge (uno en cada sentido) al agregar una conexion.

    Para el peso usaremos long
 */

import Exceptions.ENumeroNegativo;

public final class Edge {

    //Atributos
    private final int para;
    private final long weight;

    //Constructor
    public Edge(int para, long weight)throws ENumeroNegativo {
        if(para<0) throw new ENumeroNegativo("El valor de 'para' o 'to' no puede ser negativo");
        this.para=para;
        this.weight=weight;
    }

    //Getters and Setters
    public int getPara(){return para;}

    public long getWeight() {return weight;}

    @Override
    public String toString() {
        return "-> "+ para+ "(w="+weight+")";
    }
}
